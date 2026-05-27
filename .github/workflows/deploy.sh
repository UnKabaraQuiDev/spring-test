name: Deploy

on:
  workflow_dispatch:
    inputs:
      mode:
        description: "What should be deployed?"
        required: true
        default: "both"
        type: choice
        options:
          - frontend
          - backend
          - both

jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Run deploy script on server
        uses: appleboy/ssh-action@v1.0.3
        with:
          host: ${{ secrets.DEPLOY_HOST }}
          username: ${{ secrets.DEPLOY_USER }}
          key: ${{ secrets.DEPLOY_SSH_KEY }}
          port: ${{ secrets.DEPLOY_PORT || 22 }}
          script: |
            /opt/spring-test/deploy.sh "${{ github.event.inputs.mode }}"
