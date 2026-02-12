# Startup Command

Initialize full project context for Claude by reading all configuration and project files.

## Instructions

Execute the following steps in order. Do NOT skip any steps.

### Step 1: Read .claude/ Directory

Read and process ALL files inside the `.claude/` directory:

1. **Rules** - Read ALL files in `.claude/rules/`
2. **Skills** - Read ALL files in `.claude/skills/`
3. **Commands** - Read ALL files in `.claude/commands/`
4. **Agents** - Read ALL files in `.claude/agents/`

### Step 2: Read CLAUDE.md

If `CLAUDE.md` exists in the project root, read it completely.

### Step 3: Read Root Configuration Files

Read the following files if they exist:
- `README.md`
- `build.gradle` (root)
- `settings.gradle`
- `gradle.properties`

### Step 4: Scan Directory Structure

Scan and list the project directory structure up to 2-3 levels deep.
- Exclude `.gradle/`, `.git/`, `build/`, `.idea/`, `libs/`
- Focus on source directories (`src/main/java/`, `src/test/java/`)

## Output Requirements

Provide a **concise summary** including:

```
PROJECT CONTEXT LOADED

Project: [name from settings.gradle or directory name]
Tech Stack: [only what is explicitly found in files]

Directory Structure:
[2-3 level tree]

Modules:
- [List Gradle modules]

Key Conventions:
- [List rules/patterns discovered from .claude/rules/]

Available Commands:
- [List commands found in .claude/commands/]
```

## Critical Rules

- **Do NOT infer or assume** anything not explicitly found in files
- **Do NOT modify, create, or suggest changes** to any files
- **Report only what is explicitly stated** in the configuration files
- If a file or directory does not exist, note it and continue

## Final Behavior

After completing the summary, **STOP and wait** for the next instruction.
Do not proactively suggest tasks or next steps.
