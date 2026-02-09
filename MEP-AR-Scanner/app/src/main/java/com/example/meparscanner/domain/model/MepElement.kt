package com.example.meparscanner.domain.model

import com.google.ar.core.Pose

sealed class MepElement {
    abstract val id: String
    abstract val pose: Pose

    data class Pipe(
        override val id: String,
        override val pose: Pose,
        val type: PipeType, // HOT_WATER, COLD_WATER, GAS
        val diameterInches: Float,
        val lengthMeters: Float
    ) : MepElement()

    data class Wire(
        override val id: String,
        override val pose: Pose,
        val type: WireType, // PHASE, NEUTRAL, GROUND
        val gauge: Int
    ) : MepElement()

    data class Fixture(
        override val id: String,
        override val pose: Pose,
        val type: FixtureType // OUTLET, SWITCH, WC, SINK
    ) : MepElement()
}

enum class PipeType { HOT, COLD, GAS }
enum class WireType { PHASE, NEUTRAL, GROUND }
enum class FixtureType { OUTLET, SWITCH, WC, SINK, LAUNDRY, SHOWER }
