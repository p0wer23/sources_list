# Sources List App - Intent

## Core Problem
I consume a high volume of information every day, mainly from blogs and YouTube. Useful links get scattered across apps, notes, browser tabs, and temporary messages. This makes it hard to decide what deserves immediate attention, what can be consumed casually, and what requires focused time.

## Product Intent
Build an Android app that acts as a simple personal inbox for content sources. The app should let me save links quickly, classify them by effort and importance, and manage them with minimal friction.

The primary interaction model should be bracket-first navigation:
- keep the existing app name/header treatment
- place a visible `+` action on the home screen for adding a new URL
- use the home screen as a selector screen, not as an inline list screen

## Primary Goal
Create one place where I can store and organize content links into three clear brackets:
- `Unclassified`: newly saved links that still need a decision
- `Casual`: links I can consume whenever I want
- `Serious`: links that require dedicated time and attention

## User Outcome
The app should help me:
- capture links before I forget them
- reduce mental clutter
- separate low-effort reading/watching from high-effort material
- move links between brackets as my priorities change
- remove links when they are no longer useful
- mark completed items as done

Navigation should also reduce clutter:
- the home screen should show only the three bracket selectors: `Unclassified`, `Casual`, and `Serious`
- tapping a selector should open that bracket page
- the home screen should not directly render the URLs inside those bracket sections

## Product Principles
- Fast to use
- Minimal UI friction
- Local-first and private
- Focused on links, not general note-taking
- Easy to reorganize at any time

## Scope of Version 1
Version 1 should focus only on storing, classifying, moving, copying, opening, deleting, and marking source links as done. It should avoid adding extra complexity such as accounts, sync, tagging, recommendations, or social features.

For completion handling in V1:
- `Casual` should provide a way to view only read URLs
- `Serious` should provide a way to view only read URLs
