---
name: eval-harness
description: Formal evaluation framework for Claude Code sessions implementing eval-driven development (EDD) principles
---

# Eval Harness Skill

A formal evaluation framework for Claude Code sessions, implementing eval-driven development (EDD) principles.

## Philosophy

Eval-Driven Development treats evals as the "unit tests of AI development":
- Define expected behavior BEFORE implementation
- Run evals continuously during development
- Track regressions with each change
- Use pass@k metrics for reliability measurement

## Eval Types

### Capability Evals
Test if Claude can do something it couldn't before:
```markdown
[CAPABILITY EVAL: feature-name]
Task: Description of what Claude should accomplish
Success Criteria:
  - [ ] Criterion 1
  - [ ] Criterion 2
  - [ ] Criterion 3
Expected Output: Description of expected result
```

### Regression Evals
Ensure changes don't break existing functionality:
```markdown
[REGRESSION EVAL: feature-name]
Baseline: SHA or checkpoint name
Tests:
  - existing-test-1: PASS/FAIL
  - existing-test-2: PASS/FAIL
Result: X/Y passed (previously Y/Y)
```

## Grader Types

### 1. Code-Based Grader
Deterministic checks using code:
```bash
# Check if tests pass
./gradlew test && echo "PASS" || echo "FAIL"

# Check if build succeeds
./gradlew build && echo "PASS" || echo "FAIL"

# Check for anti-patterns
grep -rn "printStackTrace" --include="*.java" src/ && echo "FAIL" || echo "PASS"
```

### 2. Model-Based Grader
Use Claude to evaluate open-ended outputs:
```markdown
[MODEL GRADER PROMPT]
Evaluate the following code change:
1. Does it solve the stated problem?
2. Is it well-structured?
3. Are edge cases handled?
4. Is error handling appropriate?

Score: 1-5 (1=poor, 5=excellent)
Reasoning: [explanation]
```

### 3. Human Grader
Flag for manual review:
```markdown
[HUMAN REVIEW REQUIRED]
Change: Description of what changed
Reason: Why human review is needed
Risk Level: LOW/MEDIUM/HIGH
```

## Metrics

### pass@k
"At least one success in k attempts"
- pass@1: First attempt success rate
- pass@3: Success within 3 attempts
- Typical target: pass@3 > 90%

### pass^k
"All k trials succeed"
- Higher bar for reliability
- pass^3: 3 consecutive successes
- Use for critical paths

## Eval Workflow

### 1. Define (Before Coding)
```markdown
## EVAL DEFINITION: feature-xyz

### Capability Evals
1. Can create new module with all boilerplate
2. Can register conditions and executes
3. Can handle config reload

### Regression Evals
1. Existing modules still load
2. Test suite passes
3. Build succeeds

### Success Metrics
- pass@3 > 90% for capability evals
- pass^3 = 100% for regression evals
```

### 2. Implement
Write code to pass the defined evals.

### 3. Evaluate
```bash
# Run capability evals
./gradlew test --tests "*.NewFeatureTest"

# Run regression evals
./gradlew test

# Generate report
```

### 4. Report
```markdown
EVAL REPORT: feature-xyz
========================

Capability Evals:
  create-module:     PASS (pass@1)
  register-hooks:    PASS (pass@2)
  config-reload:     PASS (pass@1)
  Overall:           3/3 passed

Regression Evals:
  existing-modules:  PASS
  test-suite:        PASS
  build:             PASS
  Overall:           3/3 passed

Metrics:
  pass@1: 67% (2/3)
  pass@3: 100% (3/3)

Status: READY FOR REVIEW
```

## Integration

```
/eval define feature-name    # Create eval definition
/eval check feature-name     # Run current evals
/eval report feature-name    # Generate full report
```

## Best Practices

1. **Define evals BEFORE coding** - Forces clear thinking about success criteria
2. **Run evals frequently** - Catch regressions early
3. **Track pass@k over time** - Monitor reliability trends
4. **Use code graders when possible** - Deterministic > probabilistic
5. **Human review for security** - Never fully automate security checks
6. **Keep evals fast** - Slow evals don't get run
7. **Version evals with code** - Evals are first-class artifacts
