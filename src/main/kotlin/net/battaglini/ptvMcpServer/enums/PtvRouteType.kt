package net.battaglini.ptvMcpServer.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = PtvRouteType.Companion.PtvRouteTypeSerializer::class)
enum class PtvRouteType(val id: Int) {
    Train(0),
    Tram(1),
    Bus(2),
    Vline(3),
    NightBus(4),
    Unknown(99);

    companion object {
        private val map = entries.associateBy(PtvRouteType::id)

        operator fun get(id: Int) = map[id]

        class PtvRouteTypeSerializer : KSerializer<PtvRouteType> {
            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
                "net.battaglini.ptvmcpserver.enums.${PtvRouteType::class.java.name}",
                PrimitiveKind.STRING
            )

            override fun deserialize(decoder: Decoder): PtvRouteType {
                val id = decoder.decodeInt()
                return PtvRouteType[id] ?: Unknown
            }

            override fun serialize(
                encoder: Encoder,
                value: PtvRouteType
            ) {
                return encoder.encodeInt(value.id)
            }
        }
    }
}