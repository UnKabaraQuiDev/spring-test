#!/usr/bin/env bash

set -euo pipefail

DOCKER_DIR="docker"
DOCKER_BRANCH="docker"
REMOTE="origin"
PUSH=false
NO_GIT=false

while [[ $# -gt 0 ]]; do
	case "$1" in
		--push)
			PUSH=true
			shift
			;;
		--no-git)
			NO_GIT=true
			shift
			;;
		--branch)
			DOCKER_BRANCH="$2"
			shift 2
			;;
		--remote)
			REMOTE="$2"
			shift 2
			;;
		*)
			echo "Unknown argument: $1" >&2
			exit 1
			;;
	esac
done

if git rev-parse --show-toplevel >/dev/null 2>&1; then
	PROJECT_DIR="$(git rev-parse --show-toplevel)"
else
	PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
fi

cd "$PROJECT_DIR"

if ! compgen -G "target/*.jar" >/dev/null; then
	echo "No jar found in target/. Run this script after the Maven package phase." >&2
	exit 1
fi

remove_existing_docker_dir() {
	if [[ ! -e "$DOCKER_DIR" ]]; then
		return
	fi

	if git -C "$DOCKER_DIR" rev-parse --is-inside-work-tree >/dev/null 2>&1; then
		git worktree remove --force "$DOCKER_DIR"
	else
		rm -rf "$DOCKER_DIR"
	fi
}

prepare_plain_docker_dir() {
	remove_existing_docker_dir
	mkdir -p "$DOCKER_DIR/target"
}

ensure_docker_branch() {
	if git show-ref --verify --quiet "refs/heads/$DOCKER_BRANCH"; then
		return
	fi

	if git show-ref --verify --quiet "refs/remotes/$REMOTE/$DOCKER_BRANCH"; then
		git branch --track "$DOCKER_BRANCH" "$REMOTE/$DOCKER_BRANCH"
		return
	fi

	local tmp_worktree
	tmp_worktree="$(mktemp -d)"
	rmdir "$tmp_worktree"

	git worktree add --detach "$tmp_worktree" HEAD
	(
		cd "$tmp_worktree"
		git switch --orphan "$DOCKER_BRANCH"
		git rm -rf . >/dev/null 2>&1 || true
		find . -mindepth 1 -maxdepth 1 ! -name .git -exec rm -rf {} +
		git commit --allow-empty -m "Initialize docker branch"
	)
	git worktree remove --force "$tmp_worktree"
}

prepare_git_docker_dir() {
	if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
		echo "This is not a Git repository. Use -Pno-git or run with --no-git." >&2
		exit 1
	fi

	ensure_docker_branch

	if [[ -e "$DOCKER_DIR" ]]; then
		if git -C "$DOCKER_DIR" rev-parse --is-inside-work-tree >/dev/null 2>&1; then
			local branch
			branch="$(git -C "$DOCKER_DIR" branch --show-current)"
			if [[ "$branch" != "$DOCKER_BRANCH" ]]; then
				git worktree remove --force "$DOCKER_DIR"
			fi
		else
			rm -rf "$DOCKER_DIR"
		fi
	fi

	if [[ ! -e "$DOCKER_DIR" ]]; then
		git worktree add "$DOCKER_DIR" "$DOCKER_BRANCH"
	fi

	find "$DOCKER_DIR" -mindepth 1 -maxdepth 1 ! -name .git -exec rm -rf {} +
	mkdir -p "$DOCKER_DIR/target"
}

copy_docker_files() {
	cp Dockerfile "$DOCKER_DIR/"
	cp docker-compose.yml "$DOCKER_DIR/"
	cp target/*.jar "$DOCKER_DIR/target/"
}

commit_docker_branch() {
	git -C "$DOCKER_DIR" add -A

	if git -C "$DOCKER_DIR" diff --cached --quiet; then
		echo "Docker branch is already up to date."
	else
		git -C "$DOCKER_DIR" commit -m "Build docker image for $(git rev-parse --short HEAD)"
	fi
}

push_docker_branch() {
	git -C "$DOCKER_DIR" push "$REMOTE" "$DOCKER_BRANCH"
}

if [[ "$NO_GIT" == true ]]; then
	prepare_plain_docker_dir
	copy_docker_files
	echo "Created Docker build output in $DOCKER_DIR/ without Git worktree."
	exit 0
fi

prepare_git_docker_dir
copy_docker_files
commit_docker_branch

if [[ "$PUSH" == true ]]; then
	push_docker_branch
fi
