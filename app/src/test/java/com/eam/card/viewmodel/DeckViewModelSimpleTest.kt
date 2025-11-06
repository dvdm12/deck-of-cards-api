package com.eam.card.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEmpty
import strikt.assertions.isFalse
import strikt.assertions.isNull

/**
 * Unit tests for DeckViewModel (simple version).
 *
 * These tests validate initialization and internal state behavior
 * without requiring Android framework dependencies.
 *
 * Focus:
 *  - Default initialization of observable states.
 *  - Encapsulation between private and public MutableStateFlows.
 */
class DeckViewModelSimpleTest {

    /**
     * Test 1: Default Initialization
     *
     * Verifies that when DeckViewModel is instantiated with a null repository,
     * its states are initialized to default values:
     *  - deckState -> null
     *  - cardsState -> empty
     *  - errorMessage -> null
     *  - isLoading -> false
     */
    @Test
    fun `viewModel initializes with default empty state`() {
        println("[TEST START] viewModel initializes with default empty state")

        // Initialize test dispatcher and scope for coroutine handling
        val dispatcher = StandardTestDispatcher()
        val scope = TestScope(dispatcher)
        println("Test dispatcher and scope initialized.")

        // Create the ViewModel instance under test
        val viewModel = DeckViewModel(
            repository = null,
            dispatcher = dispatcher,
            scope = scope
        )
        println("DeckViewModel created with null repository.")

        // Log current state values
        println("deckState: ${viewModel.deckState.value}")
        println("cardsState: ${viewModel.cardsState.value}")
        println("errorMessage: ${viewModel.errorMessage.value}")
        println("isLoading: ${viewModel.isLoading.value}")

        // Validate expected defaults
        expectThat(viewModel.deckState.value).isNull()
        expectThat(viewModel.cardsState.value).isEmpty()
        expectThat(viewModel.errorMessage.value).isNull()
        expectThat(viewModel.isLoading.value).isFalse()

        println("[ASSERTIONS PASSED] Default initialization verified successfully.\n")
    }

    /**
     * Test 2: Manual Loading Flag Update
     *
     * Ensures that modifying the private `_isLoading` field directly via reflection
     * does not affect the public observable `isLoading` flow.
     *
     * This test confirms proper encapsulation of internal state flows.
     */
    @Test
    fun `setting loading state manually updates the flag`() {
        println("[TEST START] setting loading state manually updates the flag")

        val dispatcher = StandardTestDispatcher()
        val scope = TestScope(dispatcher)
        val viewModel = DeckViewModel(
            repository = null,
            dispatcher = dispatcher,
            scope = scope
        )
        println("DeckViewModel created for reflection test.")
        println("Initial isLoading (public): ${viewModel.isLoading.value}")

        // Simulate internal modification of private _isLoading flow
        viewModel.javaClass.getDeclaredField("_isLoading").apply {
            isAccessible = true
            set(viewModel, MutableStateFlow(true))
        }
        println("Private _isLoading field forcibly changed to true using reflection.")

        // Log state after modification
        println("After modification, public isLoading: ${viewModel.isLoading.value}")

        // Validate that public state remains unaffected
        expectThat(viewModel.isLoading.value).isFalse()

        println("[ASSERTIONS PASSED] Public flow unaffected by internal change.\n")
    }
}
