# N8N Workflow Backup System

## Overview

This GitHub Actions workflow provides automated backup of n8n workflows to a dedicated repository branch. The system securely connects to your n8n instance, downloads workflow configurations, and stores them as version-controlled JSON files.

## Features

- 🔄 **Automated Daily Backups**: Runs automatically at 21:00 Turkey time (UTC+3)
- 🎯 **Selective Filtering**: Option to backup only specific workflows by exact name
- 🔐 **Secure Authentication**: Uses API key authentication (not JWT tokens)
- 📁 **Organized Storage**: Workflows stored in dedicated `n8n-workflows-backup` branch
- 📝 **Detailed Logging**: Comprehensive logs for troubleshooting and monitoring
- 🏷️ **File Sanitization**: Safe filenames with special character replacement

## Quick Start

### 1. Setup Repository Secrets

Navigate to your GitHub repository → **Settings** → **Secrets and variables** → **Actions**

Add the following secrets:

| Secret Name | Description | Example |
|-------------|-------------|---------|
| `N8N_JWT_TOKEN` | Your n8n API key (not JWT token) | `n8n_api_key_here` |
| `N8N_URL` | Your n8n instance URL | `https://your-n8n-instance.com` |

### 2. Configure Optional Filtering

**Repository Variables** (optional):
- Go to **Settings** → **Variables** → **Actions**
- Add variable: `WORKFLOW_FILTER`
- Examples: `"New SQL Generator"`, `"Agentic_RAG_production"`

### 3. Run the Backup

**Automatic**: Runs daily at 00:00 Turkey time (21:00 UTC)

**Manual**: Go to **Actions** → **"n8n Workflows Backup"** → **"Run workflow"**

## Workflow Details

### Authentication Methods

The workflow tries authentication methods in order:
1. ❌ **JWT Bearer Token** (not supported by your n8n instance)
2. ❌ **API Key with legacy endpoint** (not supported)
3. ✅ **API Key with `/api/v1/workflows`** (your working method)

### File Structure

```
your-repo/
├── .github/
│   └── workflows/
│       └── backup_workflows.yml          # Main workflow file
├── workflow-backups/                     # Local directory (for reference)
│   └── README.md                         # Documentation
└── N8N_WORKFLOW_BACKUP.md               # This documentation
```

### Backup Branch Structure

After successful backup:

```
n8n-workflows-backup branch:
├── workflow-backups/
│   ├── Agentic_RAG_production (06p2Nmq5AmXZHKgn).json
│   ├── SQL_Generator (s5koSwZmWBWxo3xl).json
│   ├── New_SQL_Generator (cRxo6eApw5Sd8V4v).json
│   └── README.md
```

## Configuration Options

### WORKFLOW_FILTER Variable

Set this repository variable to backup only specific workflows:

| Filter Value | Behavior | Example Match |
|--------------|----------|---------------|
| `"New SQL Generator"` | Exact name match (case insensitive) | ✅ `New SQL Generator`<br>✅ `new sql generator`<br>❌ `New SQL Generator Test` |
| Empty/not set | Backup all workflows | All workflows |

### Environment Variables

| Variable | Purpose | Default |
|----------|---------|---------|
| `N8N_JWT_TOKEN` | API authentication | Required |
| `N8N_URL` | n8n instance URL | Required |
| `N8N_BASIC_USER` | Basic auth username | Optional |
| `N8N_BASIC_PASS` | Basic auth password | Optional |
| `WORKFLOW_FILTER` | Workflow name filter | Optional |

## Troubleshooting

### Common Issues

#### 1. "X-N8N-API-KEY header required" Error
- **Cause**: Using JWT token instead of API key
- **Solution**: Ensure `N8N_JWT_TOKEN` contains your n8n API key, not a JWT token

#### 2. "Permission denied" on Git Commit
- **Cause**: Pushing to protected branch
- **Solution**: Workflow automatically uses `n8n-workflows-backup` branch (fixed)

#### 3. No Workflows Found
- **Cause**: Wrong n8n URL or authentication
- **Solution**: Verify `N8N_URL` and `N8N_JWT_TOKEN` in repository secrets

#### 4. Filter Not Working
- **Cause**: Partial matching instead of exact matching
- **Solution**: Filter uses exact name matching (case insensitive)

### Checking Workflow Status

1. **View Logs**: Actions → "n8n Workflows Backup" → Click latest run
2. **Check Branch**: Switch to `n8n-workflows-backup` branch in repository
3. **Verify Files**: Browse `workflow-backups/` directory

### Log Examples

**Successful Run**:
```
Connecting to n8n API using API key authentication...
Successfully fetched workflow list (HTTP 200).
Applying workflow filter: 'New SQL Generator' (exact name match, case insensitive)
Found 1 workflows matching filter 'New SQL Generator'
Downloading workflow: New SQL Generator (s5koSwZmWBWxo3xl)
✅ Workflows committed to n8n-workflows-backup branch
```

## Advanced Usage

### Custom Scheduling

Modify the cron schedule in `backup_workflows.yml`:

```yaml
schedule:
  - cron: "0 21 * * *"  # Daily at 21:00
```

### Adding More Authentication Methods

The workflow can be extended to support additional n8n API authentication methods by adding more method blocks.

### Custom File Naming

Modify the filename generation in the workflow to change how workflows are named:

```bash
# Current: "Workflow Name (ID).json"
echo "$workflow_response" > "./workflow-backups/${name} (${id}).json"

# Alternative: "ID_Workflow_Name.json"
echo "$workflow_response" > "./workflow-backups/${id}_${name}.json"
```

## Security Considerations

- 🔐 **Secrets Management**: Store API keys as GitHub repository secrets
- 🚫 **No Token Exposure**: JWT tokens are not logged in workflow output
- 🔒 **Branch Protection**: Backups stored in separate branch from main code
- 📝 **Audit Trail**: All backup operations are logged and version controlled

## Support

For issues with the n8n workflow backup system:

1. **Check Workflow Logs**: Review the Actions tab for detailed error messages
2. **Verify Configuration**: Ensure all required secrets and variables are set
3. **Test Authentication**: Verify your n8n API key works with the API directly
4. **Review Documentation**: Check this document and the workflow file comments

## Version History

- **v1.0**: Initial implementation with JWT Bearer token support
- **v1.1**: Added API key fallback authentication
- **v1.2**: Implemented exact name filtering and improved error handling
- **v1.3**: Fixed git permissions, added dedicated backup branch
- **v1.4**: Renamed directory to `workflow-backups`, added comprehensive documentation
