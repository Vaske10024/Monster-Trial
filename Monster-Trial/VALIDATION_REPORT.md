# Validation Report

Date: 2026-05-03

## Potion balance update

- Healing Potion remains an action potion: it restores 28 HP, consumes the turn, and is limited to once per battle.
- Resistance Potion is now a quick-use potion: it costs 100 coins, is limited to once per battle, applies +25% resistance to the next incoming hit up to a 65% temporary resistance cap, and does not end the turn. The player can use it and still attack on the same move.
- Cleansing Potion remains an action potion: it removes Poison, Burn, Slow, Chilled, and Expose, consumes the turn, and is limited to once per battle.
- Potion responses now include a `consumesAction` flag so the frontend can distinguish quick-use potions from action potions.
- Battle UI copy and potion cards were updated to show `Quick use` or `Uses action`.

## Completed checks

- Frontend dependency install via `npm ci --ignore-scripts` completed.
- Frontend production build via `npm run build` passed.
- Backend Java source subset compile passed with local stubs for Spring/Jakarta annotations. This validates the changed model, DTO, data, mapper, and service classes for Java syntax/type compatibility without needing external Maven dependencies.
- Zip integrity check passed for the final archive.

## Environment limitation

A real Maven package/test run could not complete in this container because DNS resolution for Apache Maven download hosts failed:

```text
curl: (6) Could not resolve host: dlcdn.apache.org
```

In a normal networked environment, run:

```bash
cd backend
./mvnw test
./mvnw -DskipTests package
```

The project Maven wrapper is configured for Maven 3.9.15 and the backend parent is Spring Boot 3.5.14.
