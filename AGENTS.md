# Repository Guidelines

This repository contains the UI templates and assets for a car marketplace. It is organized to be Spring Boot–friendly, but the backend scaffolding is not yet included. Use this guide to stay consistent when extending the project.

## Project Structure & Module Organization

- src/main/resources/templates: Thymeleaf-ready HTML pages (e.g., `index.html`, `login.html`).
- src/main/resources/templates/static: Static assets grouped by type (`css`, `js`, `img`, `fonts`).
- Future backend (if added): `src/main/java/...` for source and `src/test/java/...` for tests.

## Build, Test, and Development Commands

- Quick preview (static):
  - `cd src/main/resources/templates && python3 -m http.server 8000`
  - Open `http://localhost:8000/index.html` to browse pages.
- Spring Boot (when backend exists):
  - Maven: `./mvnw spring-boot:run`
  - Gradle: `./gradlew bootRun`
- Asset edits: no build step required; edit files under `static/` and refresh the browser.

## Coding Style & Naming Conventions

- HTML/CSS/JS: 2-space indentation; keep lines concise; prefer semantic HTML.
- Java (when present): 4-space indentation; follow standard Spring naming.
- Filenames: use `kebab-case` for HTML/CSS/JS (e.g., `profile-setting.html`, `main.css`, `car-list.js`).
- Paths: reference assets with relative paths from templates (e.g., `static/js/main.js`).
- Formatting: run Prettier (optional) on templates/static; avoid inline scripts/styles when possible.

## Testing Guidelines

- Frontend: add lightweight smoke tests if introducing tooling (e.g., Playwright). Place under `tests/` and document commands.
- Backend (future): JUnit 5 with Spring Boot test starter; mirror package structure under `src/test/java`.
- Aim for meaningful coverage on services/controllers; keep UI tests fast and focused.

## Commit & Pull Request Guidelines

- Commits: use Conventional Commits (e.g., `feat: add dealer profile page`, `fix: correct asset path in header`).
- PRs: small, focused changes; include a clear description, linked issues, and screenshots/GIFs for UI changes.
- Check that modified templates load without console errors and asset paths resolve.

## Security & Configuration Tips

- Keep third-party libraries under `static/` up to date; remove unused files.
- When adding backend, enable Spring Security early and externalize secrets via environment variables.
- Prefer CSP-friendly patterns: no inline JS; use separate `.js` files.

