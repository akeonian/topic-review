package com.example.topicreview.data

import UiPreferences
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object HomePreferencesSerializer: Serializer<UiPreferences.HomePreferences> {

    override val defaultValue: UiPreferences.HomePreferences = UiPreferences.HomePreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UiPreferences.HomePreferences {
        try {
            return UiPreferences.HomePreferences.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", e)
        }
    }

    override suspend fun writeTo(t: UiPreferences.HomePreferences, output: OutputStream) {
        t.writeTo(output)
    }
}