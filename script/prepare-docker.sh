#!/usr/bin/env bash

set -euo pipefail

DOCKER_DIR="docker"
DOCKER_BRANCH="docker"
REMOTE="origin"
PUSH=false
NO_GIT=false
DOCKER_JAR_NAME="app.jar"

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

find_application_jar() {
	local jars=()

	while IFS= read -r jar; do
		jars+=("$jar")
	done < <(find target -maxdepth 1 -type f -name "*.jar" \
		! -name "*-sources.jar" \
		! -name "*-javadoc.jar" \
		! -name "*.original" \
		| sort)

	if [[ ${#jars[@]} -eq 0 ]]; then
		echo "No application jar found in target/. Run this script after the Maven package phase." >&2
		exit 1
	fi

	if [[ ${#jars[@]} -gt 1 ]]; then
		echo "Multiple application jars found in target/:" >&2
		printf ' - %s\n' "${jars[@]}" >&2
		echo "Remove old jars or adjust script/prepare-docker.sh." >&2
		exit 1
	fi

	printf '%s\n' "${jars[0]}"
}

ensure_clean_main_repo() {
	if ! git diff --quiet || ! git diff --cached --quiet; then
		echo "Main worktree has uncommitted changes." >&2
		echo "Commit, stash, or run with --no-git." >&2
		exit 1
	fi
}

ensure_local_docker_branch_tracks_remote() {
	git fetch "$REMOTE" "$DOCKER_BRANCH"

	if git show-ref --verify --quiet "refs/heads/$DOCKER_BRANCH"; then
		return
	fi

	if git show-ref --verify --quiet "refs/remotes/$REMOTE/$DOCKER_BRANCH"; then
		git branch --track "$DOCKER_BRANCH" "$REMOTE/$DOCKER_BRANCH"
		return
	fi

	echo "Remote branch $REMOTE/$DOCKER_BRANCH does not exist." >&2
	echo "Create it once first, then rerun this script." >&2
	exit 1
}

prepare_plain_docker_dir() {
	if [[ -e "$DOCKER_DIR" && ! -d "$DOCKER_DIR" ]]; then
		echo "$DOCKER_DIR exists but is not a directory." >&2
		exit 1
	fi

	mkdir -p "$DOCKER_DIR/target"
}

prepare_git_docker_dir() {
	if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
		echo "This is not a Git repository. Use -Pno-git or run with --no-git." >&2
		exit 1
	fi

#	ensure_clean_main_repo
	ensure_local_docker_branch_tracks_remote

	if [[ -e "$DOCKER_DIR" && ! -d "$DOCKER_DIR" ]]; then
		echo "$DOCKER_DIR exists but is not a directory." >&2
		exit 1
	fi

	if [[ ! -e "$DOCKER_DIR" ]]; then
		git worktree add "$DOCKER_DIR" "$DOCKER_BRANCH"
	fi

	if ! git -C "$DOCKER_DIR" rev-parse --is-inside-work-tree >/dev/null 2>&1; then
		echo "$DOCKER_DIR exists but is not a Git worktree." >&2
		echo "Remove it manually or run with --no-git." >&2
		exit 1
	fi

	local branch
	branch="$(git -C "$DOCKER_DIR" branch --show-current)"

	if [[ "$branch" != "$DOCKER_BRANCH" ]]; then
		echo "$DOCKER_DIR exists but is not on branch $DOCKER_BRANCH." >&2
		echo "Current branch: $branch" >&2
		exit 1
	fi

	if ! git -C "$DOCKER_DIR" diff --quiet || ! git -C "$DOCKER_DIR" diff --cached --quiet; then
		echo "$DOCKER_DIR has uncommitted changes, resetting." >&2
		git -C "$DOCKER_DIR" fetch "$REMOTE" "$DOCKER_BRANCH"
		git -C "$DOCKER_DIR" reset --hard "$REMOTE/$DOCKER_BRANCH"
		git -C "$DOCKER_DIR" clean -fd
#		echo "Commit or stash them first." >&2
#		exit 1
	fi

	git -C "$DOCKER_DIR" pull --ff-only "$REMOTE" "$DOCKER_BRANCH"

	mkdir -p "$DOCKER_DIR/target"
}

copy_docker_files() {
	local application_jar
	application_jar="$(find_application_jar)"

	cp Dockerfile "$DOCKER_DIR/Dockerfile"
	cp docker-compose.yml "$DOCKER_DIR/docker-compose.yml"
	rm -rf "$DOCKER_DIR/target"
	mkdir -p "$DOCKER_DIR/target"
	cp "$application_jar" "$DOCKER_DIR/target/$DOCKER_JAR_NAME"
}

commit_docker_branch() {
	git -C "$DOCKER_DIR" add .
	# Dockerfile docker-compose.yml "target/$DOCKER_JAR_NAME"

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