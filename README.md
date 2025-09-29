# kBorsh

[![badge-latest-release]][url-latest-release]
[![badge-kotlin]][url-kotlin]

## Borsh Kotlin Serialization Format

This library provides a Borsh serialization format for kotlinx-serialization.

## Usage

```kotlin
@Serializable
data class MyObject(val name: String, val id: Int, val price: Float)

// given a serializable object
val myObject = MyObject("Some Thing", 1234, 567.89f)

// encode it as bytes according to the Borsh.io specification
val myObjectBorshEncoded: ByteArray = Borsh.encodeToByteArray(myObject)

// decode serializable object from bytes
val myObjectDecoded: MyObject = Borsh.decodeFromByteArray<MyObject>(myObjectBorshEncoded)
```

### From Hex String
```kotlin
@Serializable
data class MyObject(val name: String, val id: Int, val price: Float)

// given a Borsh encoded hex string
val myEncodedString = "0a000000536f6d65205468696e67d2040000f6f80d44"

// decode serializable object from string
val myObjectDecodedFromString: MyObject = Borsh.decodeFromHexString<MyObject>(myObjectBorshEncodedString)
```

### From Base64
```kotlin
@Serializable
data class MyObject(val name: String, val id: Int, val price: Float)

// given a Borsh encoded Base64 string
val myEncodedBase64String = "CgAAAFNvbWUgVGhpbmfSBAAA9vgNRA=="

// can use any Base64 decoder you choose
val encodedBorshBytes: ByteArray = Base64.getDecoder().decode(myEncodedBase64String)

// decode serializable object from bytes
val myObjectDecoded: MyObject = Borsh.decodeFromByteArray<MyObject>(encodedBorshBytes)
```

<!-- TAG_VERSION -->
[badge-latest-release]: https://img.shields.io/badge/dynamic/json.svg?url=https://api.github.com/repos/funkatronics/kborsh/releases/latest&query=tag_name&label=release&color=blue

<!-- TAG_DEPENDENCIES -->
[badge-kotlin]: https://img.shields.io/badge/kotlin-2.2.20-blue.svg?logo=kotlin

[url-latest-release]: https://github.com/funkatronics/kborsh/releases/latest
[url-kotlin]: https://kotlinlang.org