# Code Quality Standards

## 1. Must Run Tests Before Completing Tasks

### The Rule
**NEVER** mark a task complete or commit code without running tests.

### Test Requirements

#### Before Every Commit
```bash
# Run type checking
npm run typecheck

# Run tests
npm run test -- --run

# Run linter
npm run lint
```

#### For New Features
```bash
# Run tests with coverage
npm run test:coverage

# Verify coverage is above 80%
# Check coverage/index.html
```

### What to Test

**Unit Tests (Required)**
- Individual functions
- Utility helpers
- Custom hooks
- Store actions
- Component logic

**Integration Tests (Required)**
- API endpoints
- Database operations
- Multiple components working together
- State management integration

**E2E Tests (Critical Paths Only)**
- User authentication flow
- Core feature workflows
- Payment/checkout processes
- Data submission forms

### Test-Driven Development (TDD)

**Mandatory workflow for new features:**

1. **RED** - Write failing test first
   ```typescript
   it('should format date correctly', () => {
     expect(formatDate('2024-01-15')).toBe('Jan 15, 2024');
   });
   ```

2. **GREEN** - Write minimal implementation
   ```typescript
   function formatDate(date: string): string {
     return new Date(date).toLocaleDateString('en-US', {
       year: 'numeric',
       month: 'short',
       day: 'numeric',
     });
   }
   ```

3. **REFACTOR** - Improve code quality
   ```typescript
   function formatDate(date: string, locale = 'en-US'): string {
     return new Date(date).toLocaleDateString(locale, {
       year: 'numeric',
       month: 'short',
       day: 'numeric',
     });
   }
   ```

4. **VERIFY** - Check coverage
   ```bash
   npm run test:coverage
   ```

### Minimum Coverage

| Type | Target | Enforcement |
|------|--------|-------------|
| Overall | 80%+ | Required |
| Critical paths | 100% | Required |
| Utilities | 100% | Required |
| UI components | 70%+ | Recommended |

### Pre-Commit Checklist
- [ ] All tests pass (`npm run test -- --run`)
- [ ] Type checking passes (`npm run typecheck`)
- [ ] Lint passes (`npm run lint`)
- [ ] Coverage above 80% for new code
- [ ] No skipped tests without documented reason
- [ ] All test files named `*.test.ts` or `*.test.tsx`

---

## 2. Must Check for Security Issues

### Security Audit Before Commit

**MANDATORY checks before every commit:**

#### 1. No Hardcoded Secrets
```typescript
// ❌ FORBIDDEN
const apiKey = "sk-proj-abc123...";
const dbPassword = "mypassword123";

// ✅ REQUIRED
const apiKey = process.env.OPENAI_API_KEY;
if (!apiKey) throw new Error('OPENAI_API_KEY not set');
```

**Check with:**
```bash
# Search for potential secrets
git diff --cached | grep -iE "(api[_-]?key|secret|password|token)"
```

#### 2. Input Validation
```typescript
// ❌ UNSAFE
function updateUser(id: string, data: any) {
  return db.user.update({ where: { id }, data });
}

// ✅ SAFE
const UpdateUserSchema = z.object({
  name: z.string().min(1).max(100),
  email: z.string().email(),
  age: z.number().int().min(0).max(150),
});

function updateUser(id: string, data: unknown) {
  const validated = UpdateUserSchema.parse(data);
  return db.user.update({ where: { id }, data: validated });
}
```

#### 3. SQL Injection Prevention
```typescript
// ❌ VULNERABLE
const query = `SELECT * FROM users WHERE email = '${email}'`;

// ✅ SAFE - Use parameterized queries
const user = await db.user.findUnique({ where: { email } });
```

#### 4. XSS Prevention
```typescript
// ❌ VULNERABLE
<div dangerouslySetInnerHTML={{ __html: userContent }} />

// ✅ SAFE
import DOMPurify from 'dompurify';
<div dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(userContent) }} />
```

#### 5. Authentication & Authorization
```typescript
// ❌ MISSING AUTH
export const deleteUser = publicProcedure
  .input(z.string())
  .mutation(({ input }) => db.user.delete({ where: { id: input } }));

// ✅ PROTECTED
export const deleteUser = protectedProcedure
  .input(z.string())
  .mutation(({ ctx, input }) => {
    if (ctx.user.id !== input && !ctx.user.isAdmin) {
      throw new TRPCError({ code: 'FORBIDDEN' });
    }
    return db.user.delete({ where: { id: input } });
  });
```

### Security Checklist
- [ ] No hardcoded secrets (keys, passwords, tokens)
- [ ] All user inputs validated (Zod schemas)
- [ ] SQL injection prevented (parameterized queries)
- [ ] XSS prevented (sanitized HTML)
- [ ] CSRF protection enabled
- [ ] Authentication on protected routes
- [ ] Authorization checked for sensitive operations
- [ ] Rate limiting on API endpoints
- [ ] Error messages don't leak sensitive data
- [ ] Dependencies have no known vulnerabilities

### Run Security Checks
```bash
# Check for vulnerabilities in dependencies
npm audit

# Fix auto-fixable issues
npm audit fix

# Check for outdated packages
npm outdated
```

---

## 3. Must Lint Before Committing

### Linting Requirements

**Before every commit, run:**
```bash
# Run linter
npm run lint

# Auto-fix issues
npm run lint:fix

# Format code
npm run format:write
```

### ESLint Configuration

**Enforce these rules:**

#### 1. No Unused Variables
```typescript
// ❌ ERROR
const unusedVar = 'foo';
function bar() {} // Never called

// ✅ CORRECT
const usedVar = 'foo';
console.log(usedVar);
```

#### 2. Consistent Naming
```typescript
// ❌ WRONG
const my_variable = 'camelCase required';
function Do_Something() {} // PascalCase for classes only

// ✅ CORRECT
const myVariable = 'camelCase';
function doSomething() {}
class MyClass {}
```

#### 3. Prefer const
```typescript
// ❌ WRONG
let name = 'John';
name; // Never reassigned

// ✅ CORRECT
const name = 'John';
```

#### 4. No Any Types
```typescript
// ❌ FORBIDDEN
function process(data: any) {
  return data.map((x: any) => x);
}

// ✅ REQUIRED
function process<T>(data: T[]) {
  return data.map((x) => x);
}
```

#### 5. Exhaustive Dependencies
```typescript
// ❌ WARNING
useEffect(() => {
  doSomething(value);
}, []); // Missing 'value'

// ✅ CORRECT
useEffect(() => {
  doSomething(value);
}, [value]);
```

### Prettier Configuration

**Auto-format on save:**
- Single quotes
- Trailing commas
- 2-space indentation
- 100 character line width
- Semicolons required

### Pre-Commit Hook (Recommended)

Install Husky for automatic checks:
```bash
npm install -D husky lint-staged

# .husky/pre-commit
npm run lint:fix
npm run format:write
npm run typecheck
npm run test -- --run
```

### Linting Checklist
- [ ] `npm run lint` passes with zero errors
- [ ] `npm run format:check` passes
- [ ] No TypeScript errors (`npm run typecheck`)
- [ ] No `any` types without justification
- [ ] No `@ts-ignore` without comment explaining why
- [ ] No disabled ESLint rules without reason
- [ ] Import statements organized (absolute paths for project files)

---

## Complete Pre-Commit Workflow

### Step-by-Step

1. **Write Tests First (TDD)**
   ```bash
   npm run test -- --watch
   # Write failing test → Write code → Test passes
   ```

2. **Check Coverage**
   ```bash
   npm run test:coverage
   # Verify 80%+ coverage
   ```

3. **Type Check**
   ```bash
   npm run typecheck
   # Fix all TypeScript errors
   ```

4. **Security Audit**
   ```bash
   # Manual checklist:
   # - No secrets
   # - Input validation
   # - No XSS/SQL injection
   # - Auth/authz in place
   ```

5. **Lint & Format**
   ```bash
   npm run lint:fix
   npm run format:write
   ```

6. **Final Test Run**
   ```bash
   npm run test -- --run
   # All tests must pass
   ```

7. **Review Changes**
   ```bash
   git diff
   # Verify no debug code, console.log, etc.
   ```

8. **Commit**
   ```bash
   git add .
   git commit -m "feat: descriptive message"
   ```

### Automated Script

Create `.claude/scripts/pre-commit.sh`:
```bash
#!/bin/bash
set -e

echo "🧪 Running tests..."
npm run test -- --run

echo "🔍 Type checking..."
npm run typecheck

echo "🎨 Linting..."
npm run lint

echo "✨ Formatting..."
npm run format:write

echo "✅ All checks passed!"
```

---

## Code Quality Metrics

### Acceptance Criteria

Before marking any work as complete:

| Metric | Minimum | Target | Status |
|--------|---------|--------|--------|
| Test Coverage | 70% | 80%+ | ⚠️ Block if <70% |
| TypeScript Errors | 0 | 0 | 🔴 Block if any |
| ESLint Errors | 0 | 0 | 🔴 Block if any |
| ESLint Warnings | <10 | 0 | 🟡 Warn if >10 |
| Security Issues | 0 | 0 | 🔴 Block if any |
| File Size | <800 lines | <400 lines | 🟡 Warn if >400 |
| Function Size | <100 lines | <50 lines | 🟡 Warn if >50 |

### Quality Gates

**Phase Gate** - Required before moving to next phase:
- [ ] All critical tests passing
- [ ] No TypeScript errors
- [ ] No ESLint errors
- [ ] No security vulnerabilities
- [ ] Code reviewed
- [ ] Documentation updated

**Production Gate** - Required before deployment:
- [ ] E2E tests passing
- [ ] Performance benchmarks met
- [ ] Security audit completed
- [ ] Dependency audit clean
- [ ] Monitoring/logging in place
- [ ] Rollback plan documented

---

## Exceptions and Waivers

### When to Skip Tests
❌ NEVER skip tests for:
- New features
- Bug fixes
- Security patches
- Data handling

✅ May skip for:
- Documentation-only changes
- Configuration files
- Build scripts (with review)

### When to Use `any`
❌ NEVER use `any` for:
- Function parameters
- Return types
- State variables

✅ May use `any` for:
- Third-party library types (temporary)
- Complex union types (document why)
- Type guards (with validation)

### When to Disable Linting
❌ NEVER disable for:
- `no-any`
- `no-unsafe-*`
- Security-related rules

✅ May disable for:
- Specific line (with comment)
- Generated code
- Test fixtures (isolated)

**Required format:**
```typescript
// eslint-disable-next-line rule-name -- Reason why this is necessary
const exception = problematicCode;
```

---

## Summary

**Never commit code without:**
1. ✅ All tests passing
2. ✅ Security audit completed
3. ✅ Linter passing
4. ✅ Type checking passing
5. ✅ Code reviewed (self or peer)

**Golden Rule:**
> If it's not tested, it's broken.
> If it's not linted, it's messy.
> If it's not secure, it's dangerous.

**Remember:**
Quality is not negotiable. Take the time to do it right the first time.
