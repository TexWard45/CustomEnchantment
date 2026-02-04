# Prisma Sync

Reliably sync Prisma schema with database (bypasses unreliable `prisma db push` on Windows).

## Steps

1. **Read Prisma schema** from `prisma/schema.prisma`
   - Parse all models and their fields
   - Extract field names, types, defaults, and constraints

2. **Query current database columns** via direct SQL:
   ```sql
   SELECT column_name, data_type, column_default, is_nullable
   FROM information_schema.columns
   WHERE table_name = '<ModelName>'
   ```

3. **Compare schema vs database**:
   - Identify missing columns
   - Identify type mismatches (warn only)
   - Report findings to user

4. **Add missing columns** via direct SQL:
   ```sql
   ALTER TABLE "<ModelName>"
   ADD COLUMN IF NOT EXISTS "<column_name>" <TYPE> <DEFAULT>
   ```

   Type mappings:
   | Prisma Type | PostgreSQL Type |
   |-------------|-----------------|
   | String | TEXT |
   | Int | INTEGER |
   | Float | DOUBLE PRECISION |
   | Boolean | BOOLEAN |
   | DateTime | TIMESTAMP(3) |
   | Json | JSONB |

5. **Regenerate Prisma client**:
   ```bash
   npx prisma generate
   ```

6. **Verify sync**:
   - Re-query database columns
   - Confirm all schema columns exist
   - Report success/failure

## Usage

**Via npm script (recommended):**
```bash
npm run db:sync
```

**Via Claude command:**
```
/prisma-sync
```

Run when:
- You see "column does not exist" errors
- After modifying `prisma/schema.prisma`
- When `npx prisma db push` fails silently

## Important Notes

- This command adds columns only (non-destructive)
- Does NOT remove columns from database
- Does NOT modify existing column types
- For destructive changes, use `npx prisma migrate dev`

## Example Output

```
Checking Generation model...
  - Missing column: isRead (Boolean, default: false)
  - Adding column via SQL...
  - Done

Checking Scenario model...
  - All columns present

Regenerating Prisma client...
Done

Sync complete: 1 column added, 0 errors
```
