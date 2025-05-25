# Slot Machine Game

A JavaFX-based slot machine game with user authentication, sound effects, and animations.

## Features

- User authentication system
- Realistic slot machine animations
- Sound effects for wins and spins
- Coin management system
- Multiple winning combinations
- Modern dark theme UI

## Winning Combinations

- Three of a kind: 100 coins
- Three sevens: 200 coins (double payout)
- Two sevens: 30 coins
- One seven: 5 coins

## Symbols

- Cherry
- Lemon
- Diamond
- Seven (Special symbol with bonus payouts)

## Technical Details

- Built with JavaFX
- Uses Maven for dependency management
- Implements MVC architecture
- Includes sound effects and animations

## Setup Instructions

1. Make sure you have Java 17 or later installed
2. Clone the repository
3. Navigate to the project directory
4. Run the following command to build the project:
   ```bash
   ./mvnw clean install
   ```
5. Run the application:
   ```bash
   ./mvnw javafx:run
   ```

## Game Controls

- Click the lever to spin
- Use "Add Coins" button to add more coins
- Each spin costs 10 coins
- Minimum coins required to play: 10

## Sound Effects

- Spin sound during reel rotation
- Win sound for regular wins
- Big win sound for three of a kind
- Add coins sound when adding coins

## UI Features

- Dark theme interface
- Animated lever
- Realistic reel spinning
- Coin counter
- Win/loss messages
- User-friendly dialogs

## Requirements

- Java 17 or later
- Maven
- JavaFX

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── slotmachineapp/
│   │               ├── SlotMachineApp.java
│   │               ├── SlotMachineController.java
│   │               ├── LoginController.java
│   │               ├── User.java
│   │               └── UserManager.java
│   └── resources/
│       ├── images/
│       │   ├── cherry.png
│       │   ├── lemon.png
│       │   ├── diamond.png
│       │   └── seven.png
│       ├── sounds/
│       │   ├── win.mp3
│       │   ├── bigwin.mp3
│       │   ├── spin.mp3
│       │   └── addcreditsound.mp3
│       └── fxml/
│           ├── LoginView.fxml
│           └── SlotMachineView.fxml
```

