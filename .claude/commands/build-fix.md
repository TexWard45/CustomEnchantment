# Build and Fix

Incrementally fix Java compilation and Gradle build errors:

1. Run build: `./gradlew build`

2. Parse error output:
   - Group by module
   - Sort by severity (compilation > dependency > test)

3. For each error:
   - Show error context (file, line, message)
   - Explain the issue
   - Propose fix
   - Apply fix
   - Re-run build for affected module
   - Verify error resolved

4. Stop if:
   - Fix introduces new errors
   - Same error persists after 3 attempts
   - User requests pause

5. Show summary:
   - Errors fixed
   - Errors remaining
   - New errors introduced

Fix one error at a time for safety!
