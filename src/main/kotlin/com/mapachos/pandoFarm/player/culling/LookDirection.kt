package com.mapachos.pandoFarm.player.culling

import org.bukkit.entity.Entity
import kotlin.math.abs

enum class LookDirection(
    internal val verticalComponent: Byte, // 0=none, 1=up, 2=down
    internal val horizontalComponent: Byte // 0-7 representing the 8 directions, -1=none
) {
    // Pure vertical
    UP(1, -1),
    DOWN(2, -1),

    // Horizontal (cardinal)
    NORTH(0, 6),
    SOUTH(0, 2),
    EAST(0, 0),
    WEST(0, 4),

    // Horizontal (diagonal)
    NORTH_EAST(0, 7),
    NORTH_WEST(0, 5),
    SOUTH_EAST(0, 1),
    SOUTH_WEST(0, 3),

    // Upward diagonals
    UP_NORTH(1, 6),
    UP_SOUTH(1, 2),
    UP_EAST(1, 0),
    UP_WEST(1, 4),
    UP_NORTH_EAST(1, 7),
    UP_NORTH_WEST(1, 5),
    UP_SOUTH_EAST(1, 1),
    UP_SOUTH_WEST(1, 3),

    // Downward diagonals
    DOWN_NORTH(2, 6),
    DOWN_SOUTH(2, 2),
    DOWN_EAST(2, 0),
    DOWN_WEST(2, 4),
    DOWN_NORTH_EAST(2, 7),
    DOWN_NORTH_WEST(2, 5),
    DOWN_SOUTH_EAST(2, 1),
    DOWN_SOUTH_WEST(2, 3);


    fun isVisibleRelativeTo(other: LookDirection): Boolean {
        // Quick check: same direction
        if (this == other) return true

        val thisVert = this.verticalComponent
        val otherVert = other.verticalComponent
        val thisHor = this.horizontalComponent
        val otherHor = other.horizontalComponent

        // Check vertical compatibility
        if (thisVert != 0.toByte() && otherVert != 0.toByte() && thisVert != otherVert) {
            return false // Opposite vertical directions
        }

        // Check horizontal compatibility
        if (thisHor >= 0 && otherHor >= 0) {
            // Use lookup table for adjacency
            return ADJACENCY_TABLE[thisHor.toInt()][otherHor.toInt()]
        }

        // At least one component matches
        return true
    }

    companion object {
        // Pitch thresholds
        private const val PITCH_STEEP_UP = -60f
        private const val PITCH_UP = -30f
        private const val PITCH_DOWN = 30f
        private const val PITCH_STEEP_DOWN = 60f

        // Yaw divisions (8 directions) - Minecraft yaw: 0°=South, 90°=West, 180°=North, 270°=East
        private const val YAW_337_5 = 337.5f  // South-East boundary
        private const val YAW_22_5 = 22.5f    // South boundary
        private const val YAW_67_5 = 67.5f    // South-West boundary
        private const val YAW_112_5 = 112.5f  // West boundary
        private const val YAW_157_5 = 157.5f  // North-West boundary
        private const val YAW_202_5 = 202.5f  // North boundary
        private const val YAW_247_5 = 247.5f  // North-East boundary
        private const val YAW_292_5 = 292.5f  // East boundary

        // Pre-calculated adjacency lookup table (8x8)
        // Each direction can see itself and adjacent directions (±45°)
        private val ADJACENCY_TABLE = arrayOf(
            booleanArrayOf(true, true, false, false, false, false, false, true),  // 0: EAST
            booleanArrayOf(true, true, true, false, false, false, false, false),  // 1: SOUTH_EAST
            booleanArrayOf(false, true, true, true, false, false, false, false),  // 2: SOUTH
            booleanArrayOf(false, false, true, true, true, false, false, false),  // 3: SOUTH_WEST
            booleanArrayOf(false, false, false, true, true, true, false, false),  // 4: WEST
            booleanArrayOf(false, false, false, false, true, true, true, false),  // 5: NORTH_WEST
            booleanArrayOf(false, false, false, false, false, true, true, true),  // 6: NORTH
            booleanArrayOf(true, false, false, false, false, false, true, true)   // 7: NORTH_EAST
        )

        // Lookup tables for fast direction mapping (horizontal x vertical)
        private val HORIZONTAL_ONLY = arrayOf(EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST, NORTH, NORTH_EAST)
        private val UP_DIRECTIONS = arrayOf(UP_EAST, UP_SOUTH_EAST, UP_SOUTH, UP_SOUTH_WEST, UP_WEST, UP_NORTH_WEST, UP_NORTH, UP_NORTH_EAST)
        private val DOWN_DIRECTIONS = arrayOf(DOWN_EAST, DOWN_SOUTH_EAST, DOWN_SOUTH, DOWN_SOUTH_WEST, DOWN_WEST, DOWN_NORTH_WEST, DOWN_NORTH, DOWN_NORTH_EAST)

        @JvmStatic
        fun Entity.canLook(other: Entity): Boolean {
            val thisDir = this.getLookDirection()
            val otherDir = other.getDirectionRelativeTo(this)
            return thisDir.isVisibleRelativeTo(otherDir)
        }

        @JvmStatic
        fun Entity.getLookDirection(): LookDirection {
            val loc = this.location
            val pitch = loc.pitch

            // Normalize yaw efficiently
            var yaw = loc.yaw % 360f
            if (yaw < 0f) yaw += 360f

            // Determine horizontal direction (8 directions)
            // Minecraft: 0°=South, 45°=SW, 90°=West, 135°=NW, 180°=North, 225°=NE, 270°=East, 315°=SE
            // Inverted by 180° to match actual player view
            val horizontalIdx = when {
                yaw < YAW_22_5 -> 6 // NORTH (was SOUTH)
                yaw < YAW_67_5 -> 7 // NORTH_EAST (was SOUTH_WEST)
                yaw < YAW_112_5 -> 0 // EAST (was WEST)
                yaw < YAW_157_5 -> 1 // SOUTH_EAST (was NORTH_WEST)
                yaw < YAW_202_5 -> 2 // SOUTH (was NORTH)
                yaw < YAW_247_5 -> 3 // SOUTH_WEST (was NORTH_EAST)
                yaw < YAW_292_5 -> 4 // WEST (was EAST)
                yaw < YAW_337_5 -> 5 // NORTH_WEST (was SOUTH_EAST)
                else -> 6 // NORTH (wraps around)
            }

            // Use lookup tables for instant direction mapping
            return when {
                pitch < PITCH_STEEP_UP -> UP
                pitch < PITCH_UP -> UP_DIRECTIONS[horizontalIdx]
                pitch < PITCH_DOWN -> HORIZONTAL_ONLY[horizontalIdx]
                pitch < PITCH_STEEP_DOWN -> DOWN_DIRECTIONS[horizontalIdx]
                else -> DOWN
            }
        }

        @JvmStatic
        fun Entity.getDirectionRelativeTo(other: Entity): LookDirection {
            val thisLoc = this.location
            val otherLoc = other.location

            val deltaX = otherLoc.x - thisLoc.x
            val deltaY = otherLoc.y - thisLoc.y
            val deltaZ = otherLoc.z - thisLoc.z

            val absDeltaX = abs(deltaX)
            val absDeltaY = abs(deltaY)
            val absDeltaZ = abs(deltaZ)

            val maxHorizontal = if (absDeltaX > absDeltaZ) absDeltaX else absDeltaZ

            // Strong vertical component (>2x horizontal)
            if (absDeltaY > maxHorizontal * 2.0) {
                return if (deltaY > 0) UP else DOWN
            }

            // Determine horizontal direction index
            // In Minecraft: +X=East, -X=West, +Z=South, -Z=North
            val horizontalIdx = when {
                absDeltaX > absDeltaZ * 2.4 -> if (deltaX > 0) 0 else 4 // EAST(+X) or WEST(-X)
                absDeltaZ > absDeltaX * 2.4 -> if (deltaZ > 0) 2 else 6 // SOUTH(+Z) or NORTH(-Z)
                else -> { // Diagonal
                    when {
                        deltaX > 0 && deltaZ > 0 -> 1 // SOUTH_EAST (+X, +Z)
                        deltaX < 0 && deltaZ > 0 -> 3 // SOUTH_WEST (-X, +Z)
                        deltaX < 0 && deltaZ < 0 -> 5 // NORTH_WEST (-X, -Z)
                        else -> 7 // NORTH_EAST (+X, -Z)
                    }
                }
            }

            // Use lookup tables for instant mapping
            return when {
                absDeltaY > maxHorizontal * 0.5 -> {
                    if (deltaY > 0) UP_DIRECTIONS[horizontalIdx]
                    else DOWN_DIRECTIONS[horizontalIdx]
                }
                else -> HORIZONTAL_ONLY[horizontalIdx]
            }
        }
    }
}