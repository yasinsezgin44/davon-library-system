# N8N Workflow Backups

This directory contains automated backups of your n8n workflows.

## How It Works

- **Automated Backup**: GitHub Actions runs daily at 00:00 Turkey time (21:00 UTC) to backup your n8n workflows
- **Storage**: Workflows are committed to the `n8n-workflows-backup` branch of this repository
- **Format**: Each workflow is saved as a JSON file with the format: `WorkflowName (ID).json`

## File Structure

```
workflow-backups/
├── README.md                           # This documentation
├── Agentic_RAG_production (ID).json    # Workflow backups
├── SQL_Generator (ID).json
└── ...
```

## Accessing Your Backups

1. **Via GitHub Web Interface**:
   - Go to your repository
   - Switch branch to `n8n-workflows-backup`
   - Browse the `workflow-backups/` directory

2. **Via Git Command Line**:
   ```bash
   git checkout n8n-workflows-backup
   ls workflow-backups/
   ```

## Workflow Configuration

- **Schedule**: Daily at 21:00 Turkey time
- **Manual Trigger**: Available in GitHub Actions tab
- **Authentication**: Uses API key authentication with `/api/v1/workflows` endpoint
- **Filtering**: Optional exact name matching via `WORKFLOW_FILTER` repository variable
