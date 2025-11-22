package com.indiewalkabout.cosmoraiders.domain

// State machine that manages game state transitions
class GameStateMachine(initialState: GameState = GameState.Idle) {
    private var currentState: GameState = initialState
        private set

    // Process a game action and update the state accordingly
    // @return  new game state after processing the action
    fun processAction(action: GameAction): GameState {
        currentState = when (val current = currentState) {
            is GameState.Idle -> handleIdleState(current, action)
            is GameState.Playing -> handlePlayingState(current, action)
            is GameState.GameOver -> handleGameOverState(current, action)
            is GameState.LevelComplete -> handleLevelCompleteState(current, action)
        }
        return currentState
    }

    private fun handleIdleState(state: GameState.Idle, action: GameAction): GameState {
        return when (action) {
            is GameAction.StartGame -> GameState.Playing()
            else -> state
        }
    }

    private fun handlePlayingState(state: GameState.Playing, action: GameAction): GameState {
        return when (action) {
            is GameAction.PauseGame -> state.copy(isPaused = true)
            is GameAction.ResumeGame -> state.copy(isPaused = false)
            is GameAction.EndGame -> GameState.GameOver(
                finalScore = state.score,
                levelReached = state.level
            )
            is GameAction.CompleteLevel -> GameState.LevelComplete(
                level = state.level,
                nextLevel = state.level + 1,
                score = state.score
            )
            is GameAction.UpdateScore -> state.copy(score = state.score + action.points)
            is GameAction.LoseLife -> {
                if (action.remainingLives <= 0) {
                    GameState.GameOver(
                        finalScore = state.score,
                        levelReached = state.level
                    )
                } else {
                    state.copy(lives = action.remainingLives)
                }
            }
            is GameAction.SetPaused -> state.copy(isPaused = action.isPaused)
            else -> state
        }
    }

    private fun handleGameOverState(state: GameState.GameOver, action: GameAction): GameState {
        return when (action) {
            is GameAction.StartGame -> GameState.Playing()
            else -> state
        }
    }

    private fun handleLevelCompleteState(state: GameState.LevelComplete, action: GameAction): GameState {
        return when (action) {
            is GameAction.StartNextLevel -> GameState.Playing(
                score = state.score,
                level = state.nextLevel
            )
            is GameAction.EndGame -> GameState.GameOver(
                finalScore = state.score,
                levelReached = state.level
            )
            else -> state
        }
    }

    // current game state
    fun getCurrentState(): GameState = currentState
}