package org.niklasunrau.pqcmessenger.domain.crypto.mceliece

import cc.redberry.rings.poly.univar.UnivariatePolynomial
import cc.redberry.rings.poly.univar.UnivariatePolynomialZp64
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import org.niklasunrau.pqcmessenger.domain.crypto.mceliece.McEliece.ff2m

class ElementSerializer : KSerializer<UnivariatePolynomialZp64> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Element", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: UnivariatePolynomialZp64) {
        val string = value.toBinary()
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): UnivariatePolynomialZp64 {
        val coeffs = decoder.decodeString().toLongArray()
        return UnivariatePolynomialZp64.create(2, coeffs)
    }
}

@OptIn(ExperimentalSerializationApi::class)
class PolySerializer : KSerializer<UnivariatePolynomial<UnivariatePolynomialZp64>> {

    private val delegateSerializer = serializer<List<String>>()
    override val descriptor: SerialDescriptor = SerialDescriptor("Poly", delegateSerializer.descriptor)
    override fun serialize(encoder: Encoder, value: UnivariatePolynomial<UnivariatePolynomialZp64>) {
        val data = List(value.size()) { value[it].toBinary() }
        encoder.encodeSerializableValue(delegateSerializer, data)
    }

    override fun deserialize(decoder: Decoder): UnivariatePolynomial<UnivariatePolynomialZp64> {
        val coeffsInString = decoder.decodeSerializableValue(delegateSerializer)
        val coeffs = coeffsInString.map { UnivariatePolynomialZp64.create(2, it.toLongArray()) }.toTypedArray()
        return UnivariatePolynomial.create(ff2m, *coeffs)
    }
}

@OptIn(ExperimentalSerializationApi::class)
class PermMatrixSerializer : KSerializer<Array<LongArray>> {

    private val delegateSerializer = IntArraySerializer()
    override val descriptor: SerialDescriptor = SerialDescriptor("Perm", delegateSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Array<LongArray>) {
        val data = IntArray(value.size) { value[it].indexOf(1) }
        encoder.encodeSerializableValue(delegateSerializer, data)
    }

    override fun deserialize(decoder: Decoder): Array<LongArray> {
        val list = decoder.decodeSerializableValue(delegateSerializer)
        return Array(list.size) { row -> LongArray(list.size) { if (list[row] == it) 1 else 0 } }
    }

}

@OptIn(ExperimentalSerializationApi::class)
class MatrixSerializer : KSerializer<Array<LongArray>> {
    private val delegateSerializer = serializer<List<String>>()
    override val descriptor: SerialDescriptor = SerialDescriptor("Matrix", delegateSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Array<LongArray>) {
        val data = List(value.size) { value[it].joinToString("") }
        encoder.encodeSerializableValue(delegateSerializer, data)
    }

    override fun deserialize(decoder: Decoder): Array<LongArray> {
        val list = decoder.decodeSerializableValue(delegateSerializer)
        return Array(list.size) { list[it].toLongArray() }
    }
}