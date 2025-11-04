## 🔀 GIT WORKFLOW & BRANCHING STRATEGY

### Branch Structure

```
main (production)
  ↑
develop (integration)
  ↑
feature/* (development)
hotfix/* (urgent fixes)
release/* (release prep)
```

### Feature Branch Workflow

```bash
# 1. Start new feature (from develop)
git checkout develop
git pull origin develop
git checkout -b feature/FTMS-123-customer-registration

# 2. Work on feature
git add .
git commit -m "feat(customer): implement registration endpoint

- Add Customer entity with JPA
- Create CustomerService with validation
- Implement POST /customers endpoint
- Add unit tests

FTMS-123"

# 3. Keep feature branch updated
git fetch origin
git rebase origin/develop

# 4. Push feature branch
git push origin feature/FTMS-123-customer-registration

# 5. Create Pull Request on GitHub
```

### Commit Message Convention

**Format:**
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Formatting, missing semi colons, etc
- `refactor`: Code restructure
- `test`: Adding tests
- `chore`: Build process or auxiliary tool changes

**Example:**
```
feat(transaction): add idempotency key validation

- Implement UUID-based idempotency
- Add database unique constraint
- Create DuplicateTransactionException
- Add integration tests

FTMS-234
```

