package main.kotlin

import kotlin.math.*
import kotlin.random.Random

class BattleRoyale(
		val capacity: Int,
		val weights: IntArray,
		val profits: IntArray,
		val n: Int,
		private val alpha: Double = 0.8, // for GRASP, initial solution randomness

		// battle royale specific parameters
		private val maximumPlayers: Int = 100,
		private val safeAreaInitialRadius: Int = 100,
		private var center: Pair<Double, Double> = Pair(0.0, 0.0),
		private var dynamicCenter: Boolean = false,
		private val shrinkRate: Double = 0.5,
		private val maxZoneDamage: Double = 0.1,
		private val playerMaxHP: Int = 100,
		private val playerVision: Int = 2,
		private val playerMaxSpeed: Double = 1.0,
		private val playerMinSpeed: Double = 0.5,
		private val playerMovementLiberty: Double = 0.25,
		) {

	// "Global" variables
	private val ks = LocalSearchKnapsack(capacity, weights, profits, n)
	private val grasp = GraspKnapsack(capacity, weights, profits, n)
	private var bestSolution = IntArray(n) { 0 }
	private var bestValue = 0
	private var endTime = Long.MAX_VALUE

	// Game specific variables
	private var safeAreaRadius = safeAreaInitialRadius.toDouble()
	private var players: List<Player> = emptyList()

	inner class Player(val id: Int, initialSolution: IntArray, var hp: Int, var x: Double = 0.0, var y: Double = 0.0) {
		var fitness: Int = 0

		val alive: Boolean
		get() = hp > 0

		var solution: IntArray = initialSolution.copyOf()
		set(value) {
			field = value
			fitness = calculateFitness()
		}

		init {
			fitness = calculateFitness()
		}

		fun move() {
			// Calculate the angle towards the center
			val angleToCenter = atan2(center.second - this.y, center.first - this.x)

			// Generate a random angle
			val randomAngle = Random.nextDouble(0.0, 2 * PI)

			// Blend the random angle and the angle towards the center based on movementLiberty
			val blendedAngle = angleToCenter * (1 - playerMovementLiberty) + randomAngle * playerMovementLiberty

			// Determine the distance to move
			val distance = Random.nextDouble(playerMinSpeed, playerMaxSpeed)

			// Calculate the new position
			val newX = this.x + distance * cos(blendedAngle)
			val newY = this.y + distance * sin(blendedAngle)

			// Update the player's position
			this.x = newX
			this.y = newY
		}

		private fun calculateFitness(): Int {
			if (!ks.isValidSolution(solution)) return 0
			return ks.calculateFitness(solution)
		}

		fun getDamage(): Double {
			return if (bestValue == 0) 1.0 else fitness.toDouble() / bestValue
		}
	}

	private fun generateInitialPlayers(playerSize: Int): List<Player> {
		// initial players with random solutions
		// each player has a random position in the safe area
		// each player has full HP
		val players = mutableListOf<Player>()
		for (i in 0 until playerSize) {
			val solution = grasp.generateRandomizedGreedySolution(alpha=alpha)

			// Distance from the center
			val xDistance = Random.nextDouble(-safeAreaRadius, safeAreaRadius)
			val yDistance = Random.nextDouble(-safeAreaRadius, safeAreaRadius)

			// Position of the player
			val x = center.first + xDistance
			val y = center.second + yDistance

			players.add(Player(i, solution, playerMaxHP, x, y))
		}
		return players
	}

	private fun updateSafeArea() {
		// relocate the center if it is dynamic
		if (dynamicCenter) {
			// Calculate the x and y offsets
			val offsetX = Random.nextDouble(-safeAreaRadius, safeAreaRadius)
			val offsetY = Random.nextDouble(-safeAreaRadius, safeAreaRadius)
		
			// Calculate the new center
			val newCenter = Pair(center.first + offsetX, center.second + offsetY)
		
			// Update the center variable
			center = newCenter
		}

		// shrink the safe area
		safeAreaRadius = maxOf(0.0, safeAreaRadius - shrinkRate)
	}

	private fun gameLoop() {
		updateSafeArea()

		// Save fighting and looting players for later
		val fightingPlayers = mutableListOf<Player>()
		val lootingPlayers = mutableMapOf<Int, Player>()


		// Player movement
		for (player in players) {
			player.move()
			val distanceX = player.x - center.first
			val distanceY = player.y - center.second

			// Players outside the safe area take damage, players inside heal
			if (sqrt(distanceX*distanceX + distanceY*distanceY) > safeAreaRadius) {
				// damage calculated based on area radius relative to initial radius
				// increases as the safe area shrinks
				val damage = (safeAreaInitialRadius - safeAreaRadius) / safeAreaInitialRadius * maxZoneDamage*playerMaxHP
				player.hp = maxOf(player.hp-damage, 0.0).toInt()
			} else {
				player.hp = minOf(player.hp+1, playerMaxHP)
			}

			if (player.alive) {
				lootingPlayers[player.id] = player
			}
		}

		// Check players who are in vision range
		for (i in players.indices) {
			val player = players[i]

			if (!player.alive) continue

			for (j in i+1 until players.size) {
				val otherPlayer = players[j]

				if (!otherPlayer.alive) continue

				val distanceX = player.x - otherPlayer.x
				val distanceY = player.y - otherPlayer.y
				val distance = sqrt(distanceX*distanceX + distanceY*distanceY)
				if (distance <= playerVision) {
					// Fight if the other player is in vision range
					fightingPlayers.add(player)
					fightingPlayers.add(otherPlayer)

					// Remove players from looting
					lootingPlayers.remove(player.id)
					lootingPlayers.remove(otherPlayer.id)
				}
			}
		}

		// Fighting phase
		val winners = if (fightingPlayers.isNotEmpty()) fight(fightingPlayers) else emptyList()

		// Looting phase
		players = loot(lootingPlayers.values.toList(), winners)
	}

	private fun fight(fighters: List<Player>): List<Player> {
		val winners = mutableListOf<Player>()
		val pool = fighters.toMutableList()

		while (pool.size > 0) {
			val fighter1 = pool.random()
			pool.remove(fighter1)

			for (fighter2 in pool) {
				// Each player deals random damage according to their fitness
				// The player with the highest fitness deals more damage
				val damage1 = Random.nextDouble(0.0, fighter1.getDamage()*playerMaxHP/10)
				val damage2 = Random.nextDouble(0.0, fighter2.getDamage()*playerMaxHP/10)

				// Deal damage to each player
				fighter1.hp = maxOf(fighter1.hp - damage2, 0.0).toInt()
				fighter2.hp = maxOf(fighter2.hp - damage1, 0.0).toInt()

				// If a player dies, scavenge their solution with the other player
				if (fighter1.alive && !fighter2.alive) {
					fighter1.solution = scavenge(fighter1.solution, fighter2.solution)
				} else if (!fighter1.alive && fighter2.alive) {
					fighter2.solution = scavenge(fighter2.solution, fighter1.solution)
				}
			}

			// Check if the player is still alive
			if (fighter1.alive) {
				winners.add(fighter1)
			}
		}
		
		return winners
	}

	private fun scavenge(winner: IntArray, loser: IntArray): IntArray {
		// Scavenge the loser's solution
		// The winner gets a random subset of the loser's solution
		// up to half of the loser's solution
		val subset = loser.toList().indices.shuffled().take(Random.nextInt(0, loser.size/2))
		val newSolution = winner.copyOf()
		for (i in subset) {
			newSolution[i] = loser[i]
		}
		return newSolution
	}

	private fun loot(lootingPlayers: List<Player>, winners: List<Player>): List<Player> {
		// given a list of players that are looting
		// and a list of winners from the fight
		// everyone is able to loot, but the winners have less time to do so
		for (player in lootingPlayers) {
			player.solution = ks.localSearch(player.solution, endTime = endTime, maxIterations = SHORT_MAX_ITERATIONS)
			val currentValue = player.fitness
			if (currentValue > bestValue) {
				bestValue = currentValue
				bestSolution = player.solution.copyOf()
			}
		}

		// Winners have less time to loot
		for (player in winners) {
			player.solution = ks.localSearch(player.solution, endTime = endTime, maxIterations = SHORT_MAX_ITERATIONS/2)
			val currentValue = player.fitness
			if (currentValue > bestValue) {
				bestValue = currentValue
				bestSolution = player.solution.copyOf()
			}
		}

		return lootingPlayers + winners
	}

	fun run(maxIterations: Int = -1, endTime: Long = -1, verbose: Boolean = false): IntArray {
		this.endTime = if (endTime == -1L) System.currentTimeMillis() + TIME_LIMIT_MS else endTime
		val iterLimit = if (maxIterations == -1) MAX_ITERATIONS else maxIterations

		// Drop players in the map
		players = generateInitialPlayers(maximumPlayers)

		// Save the best solution
		bestSolution = players.maxByOrNull { it.fitness }!!.solution.copyOf()
		bestValue = ks.calculateFitness(bestSolution)

		// Main loop
		for (i in 0 until iterLimit) {
			if (System.currentTimeMillis() > endTime) break

			if (verbose) {
				println("Iteration $i")
				println("\t${players.size} players remaining with HP: ${players.map { it.hp }}")
				println("\tSafe area radius: $safeAreaRadius")
				println("\tZone damage: ${(safeAreaInitialRadius - safeAreaRadius) / safeAreaInitialRadius * maxZoneDamage*playerMaxHP}")
			}

			gameLoop()

			if (verbose && players.size <= 1) {
				println("###############################################")
				println("\tLast player standing!")
				println("\tGame ended after $i iterations")
				println("\tSafe area radius: $safeAreaRadius")
				println("###############################################")
				break
			}
		}

		return bestSolution
	}
}
/*
fun main() {
	val capacity = 50
	val weights = intArrayOf(10, 20, 30)
	val profits = intArrayOf(60, 100, 120)
	val n = weights.size

	val br = BattleRoyale(capacity, weights, profits, n)
	val res = br.run(maxIterations = 1000, endTime = System.currentTimeMillis() + 60_000, verbose = true)
	println("Best solution: ${res.toList()}")
}
*/