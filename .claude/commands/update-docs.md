# Update Documentation

Sync documentation from source-of-truth:

1. Read build.gradle files
   - Generate module dependency table
   - Include plugin versions and configurations

2. Read CLAUDE.md
   - Verify build commands are current
   - Verify module table matches actual modules

3. Generate docs/CONTRIB.md with:
   - Development workflow
   - Build and test commands
   - Module structure overview
   - Testing procedures (JUnit 5, MockBukkit)

4. Generate docs/RUNBOOK.md with:
   - Server deployment procedures
   - Plugin installation and configuration
   - Common issues and fixes
   - Config migration guide

5. Identify obsolete documentation:
   - Find docs not modified in 90+ days
   - List for manual review

6. Show diff summary

Single source of truth: build.gradle files and CLAUDE.md
