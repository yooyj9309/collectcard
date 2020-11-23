package com.rainist.collectcard.common.converter

import com.rainist.collectcard.common.crypto.AesGcmEncrypt
import com.rainist.collectcard.common.service.KeyManagementService
import javax.persistence.AttributeConverter

open class AttributeEncryptConverter(

    var keyManagementService: KeyManagementService,
    var keyAlias: KeyManagementService.KeyAlias

) : AttributeConverter<String?, String?> {

    override fun convertToDatabaseColumn(attribute: String?): String? {

        return if (attribute.isNullOrEmpty()) {
            attribute
        } else {
            AesGcmEncrypt.encryptStringBase64(
                keyManagementService.getSecret(keyAlias),
                keyManagementService.getIv(keyAlias),
                attribute
            )
        }
    }

    override fun convertToEntityAttribute(dbData: String?): String? {

        return if (dbData.isNullOrEmpty()) {
            dbData
        } else {
            AesGcmEncrypt.decryptStringBase64(
                keyManagementService.getSecret(keyAlias),
                keyManagementService.getIv(keyAlias),
                dbData
            )
        }
    }
}
