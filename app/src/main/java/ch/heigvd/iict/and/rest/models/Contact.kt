package ch.heigvd.iict.and.rest.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.*


object CalendarSerializer : KSerializer<Calendar> {
    private val formatter =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)

    override val descriptor =
        PrimitiveSerialDescriptor("Calendar", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Calendar) {
        formatter.timeZone = value.timeZone
        encoder.encodeString(formatter.format(value.time))
    }

    override fun deserialize(decoder: Decoder): Calendar {
        return Calendar.getInstance().apply {
            time = formatter.parse(decoder.decodeString())!!
        }
    }
}

@Serializable
@Entity
data class Contact(@PrimaryKey(autoGenerate = true) var id: Long? = null,
              var name: String,
              var firstname: String?,
              @Serializable(with = CalendarSerializer::class)
              var birthday : Calendar?,
              var email: String?,
              var address: String?,
              var zip: String?,
              var city: String?,
              var type: PhoneType?,
              var phoneNumber: String?)