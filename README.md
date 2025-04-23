# Davon Library System

## Project Overview

This repository contains the codebase for the Davon Library System. This project is being developed as part of an internship program with the primary goals of:

1.  Building a functional Library Management System.
2.  Learning and implementing effective version control practices, specifically the Git Flow methodology.
3.  Establishing a structured approach to web project development.

The development will follow the tasks outlined in the internship plan.

## Features (Planned)

-   Add new books to the library.
-   Remove books from the library.
-   Search for books by title, author, or ISBN.
-   Manage user accounts.
-   (Potentially) Book borrowing and return tracking.

## Technology Stack (Planned)

-   **Backend:** Python (likely with a web framework)
-   **Frontend:** (Specify planned frontend technologies like HTML, CSS, JavaScript, or a framework)
-   **Database:** (Specify planned database technology)

## Getting Started

Instructions on how to set up and run the project locally will be added here as development progresses according to the internship tasks.

## Contributing

Contribution guidelines, following the Git Flow process learned during the internship, will be added here.

## License

(Choose and state your preferred license, e.g., MIT)


## Git Workflow

This project utilizes the **Git Flow** branching model for managing development and releases. The core principles of this workflow are outlined below:

### Branches

* **`main` **: This branch contains the production-ready code. Commits to `main` are typically made only when finishing a release.
* **`develop`**: This branch represents the latest development state. All new features are merged into `develop` once completed. It serves as an integration branch for the next release.
* **`feature/`***: Feature branches are created off of `develop` to develop new features. They are merged back into `develop` when development is complete. Naming convention is typically `feature/<descriptive-name>`.
* **`release/`***: Release branches are created off of `develop` to prepare for a new production release. Only bug fixes, documentation updates, and other release-related tasks are done on these branches. They are merged into both `main` and `develop` upon completion and tagged with a version number. Naming convention is `release/<version-number>`.
* **`hotfix/`***: Hotfix branches are created off of `main` to quickly address critical bugs in production. They are merged into both `main` and `develop` upon completion and tagged. Naming convention is `hotfix/<descriptive-name>`.

### Workflow Summary

1.  Development for new features takes place on `feature` branches, branched from `develop`.
2.  Completed `feature` branches are merged into `develop`.
3.  When preparing for a release, a `release` branch is created from `develop`.
4.  Release-specific work (bug fixes, etc.) is done on the `release` branch.
5.  The `release` branch is merged into `main` and tagged.
6.  The `release` branch is also merged back into `develop`.
7.  Hotfixes for production bugs are developed on `hotfix` branches, branched from `main`.
8.  `hotfix` branches are merged into both `main` and `develop` and tagged.

This workflow helps maintain a clear separation between ongoing development, upcoming releases, and production fixes.
