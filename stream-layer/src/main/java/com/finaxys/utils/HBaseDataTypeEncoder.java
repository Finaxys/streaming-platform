package com.finaxys.utils;


import org.apache.hadoop.hbase.types.*;
import org.apache.hadoop.hbase.util.SimplePositionedMutableByteRange;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class HBaseDataTypeEncoder implements Serializable {

	private static final long serialVersionUID = 3606586559755804856L;
	private final DataType<String> strDataType = new RawStringTerminated("\0");
	private final DataType<Integer> intDataType = new RawInteger();
	private final DataType<Long> longDataType = new RawLong();
	private final DataType<Double> doubleDataType = new RawDouble();
	private final DataType<byte[]> charType = OrderedBlobVar.ASCENDING;
	private final DataType<Byte> boolDataType = new RawByte();

	@NotNull
	public byte[] encodeString(@NotNull String value) {
		return encode(strDataType, value);
	}

	@NotNull
	public byte[] encodeInt(int value) {
		return encode(intDataType, value);
	}

	@NotNull
	public byte[] encodeBoolean(boolean value) {
		return encode(boolDataType, (byte) (value ? 1 : 0));
	}

	@NotNull
	public byte[] encodeLong(long value) {
		return encode(longDataType, value);
	}

	@NotNull
	public byte[] encodeDouble(@NotNull Number value) {
		return encode(doubleDataType, value.doubleValue());

	}

	@NotNull
	public byte[] encodeChar(@NotNull char value) {
		return encode(charType, charToBytes(value));
	}

	/**
	 * Return an array of 2 bytes
	 * 
	 * @param c
	 *            ascii or not (example : 'é', '^o', 'ç'...)
	 * @return encoded char as byte array
	 */
	@NotNull
	private static byte[] charToBytes(@NotNull Character c) {
		byte[] b = new byte[2];
		b[0] = (byte) ((c & 0xFF00) >> 8);
		b[1] = (byte) (c & 0x00FF);
		return b;
	}

	@NotNull
	private <T> byte[] encode(@NotNull DataType<T> dt, @NotNull T value) {
		SimplePositionedMutableByteRange sbpr = new SimplePositionedMutableByteRange(
				dt.encodedLength(value));
		dt.encode(sbpr, value);
		return sbpr.getBytes();
	}

	@NotNull
	private <T> T decode(@NotNull DataType<T> dt, @NotNull byte[] value) {
		SimplePositionedMutableByteRange sbpr = new SimplePositionedMutableByteRange(
				value);
		T result = dt.decode(sbpr);
		return result;

	}

	@NotNull
	public String decodeString(@NotNull byte[] value) {
		return decode(strDataType, value);
	}

	@NotNull
	public int decodeInt(@NotNull byte[] value) {
		return decode(intDataType, value);
	}

	@NotNull
	public Boolean decodeBoolean(@NotNull byte[] value) {
		return decode(boolDataType, value) == 1;
	}

	@NotNull
	public long decodeLong(@NotNull byte[] value) {
		return decode(longDataType, value);
	}

	@NotNull
	public Number decodeDouble(@NotNull byte[] value) {
		return decode(doubleDataType, value);
	}

	@NotNull
	public char decodeChar(@NotNull byte[] value) {
		byte[] bytes = decode(charType, value);
		return (char) (((bytes[0] & 0x00FF) << 8) + (bytes[1] & 0x00FF));
	}

}