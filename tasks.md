Based on the project requirements in the PDF and analyzing your current codebase in the `final-wip` branch, here's what appears to be already implemented:

**Already Implemented:**
- Basic GUI framework (StartScreen, GameScreen, HUDOverlay, GameOverScreen using Swing)
- Player class with movement (left/right/jump/prone), shooting mechanics
- Bullet and Projectile classes
- Boss classes (Boss1, Boss2, Boss3) with basic structure
- Entity system with Sprite and SpriteAnimation components
- Font management
- Maven build configuration with JavaFX dependencies
- Basic launcher and screen transitions

**Remaining To-Do List (based on requirements, excluding implemented features):**

1. **Game Loop & Rendering System**
   - Implement proper game loop with delta time calculations
   - Integrate JavaFX components with Swing GUI for sprite rendering
   - Add background rendering and level backgrounds

2. **Boss AI & Attack Patterns**
   - Implement Boss1 (Defense Wall) attack patterns and movement
   - Implement Boss2 (Java) attack patterns and movement
   - Implement Boss3 custom design with unique attacks
   - Add projectile spawning and management for bosses

3. **Collision Detection System**
   - Implement collision detection between bullets/projectiles and entities
   - Add player-boss collision (one-hit kill)
   - Add boundary collision for entities

4. **Game State Management**
   - Implement level progression (boss 1 → 2 → 3)
   - Add life system (3 lives, game over on death)
   - Implement win condition (all bosses defeated)

5. **Scoring System**
   - Implement score tracking and display updates
   - Add points for hitting/destroying bosses
   - Display score in HUD

6. **Minions System** (for higher rubric scores)
   - Add regular enemies before each boss fight
   - Implement minion AI and spawning
   - Clear minions before boss appears

7. **Special Attacks & Effects**
   - Add special attack animations (spread shot, laser, etc.)
   - Implement explosion effects on hits
   - Add visual feedback for damage/hits

8. **Sound Effects**
   - Add bullet firing sounds
   - Add hit/explosion sounds
   - Add player death sounds

9. **Logging System**
   - Implement logging for player movements
   - Add logging for character actions and scoring
   - Use different log levels appropriately

10. **Exception Handling**
    - Add centralized exception handling
    - Create custom exception classes
    - Handle resource loading failures gracefully

11. **Unit Testing**
    - Write tests for character movements
    - Add tests for scoring system
    - Test collision detection

12. **Build Management Enhancements**
    - Ensure executable JAR creation works
    - Add proper resource packaging

**Suggested Branch Names for Implementation:**

- `feature/game-loop-rendering` - Game loop and sprite rendering integration
- `feature/boss-ai-system` - Boss AI, attack patterns, and projectile management
- `feature/collision-system` - Collision detection between all entities
- `feature/game-state-management` - Level progression, lives, win/lose conditions
- `feature/scoring-system` - Score tracking and HUD updates
- `feature/minions-system` - Regular enemies before boss fights
- `feature/special-attacks-effects` - Special attacks and explosion effects
- `feature/sound-effects` - Audio system implementation
- `feature/logging-system` - Comprehensive logging implementation
- `feature/exception-handling` - Centralized exception management
- `feature/unit-testing` - Test suite development
- `feature/build-optimization` - Build and packaging improvements

Each branch should be created from `final-wip` and focus on one feature area to maintain clean development history.