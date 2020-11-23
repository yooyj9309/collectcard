package com.rainist.collectcard.grpc.client

import com.github.banksalad.idl.apis.v1.cipher.CipherGrpc
import com.github.banksalad.idl.apis.v1.cipher.CipherProto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("production", "development", "staging", "local")
class CipherClientService(
    @Qualifier("cipherBlockingStub")
    val cipherBlockingStub: CipherGrpc.CipherBlockingStub
) {

    fun getEncryptedDbTableCipherKey(dbName: String?, tableName: String?): CipherProto.GetEncryptedDbTableCipherKeyResponse? {
        val request: CipherProto.GetEncryptedDbTableCipherKeyRequest = CipherProto.GetEncryptedDbTableCipherKeyRequest.newBuilder()
            .setDbName(dbName)
            .setTableName(tableName)
            .build()
        return cipherBlockingStub.getEncryptedDbTableCipherKey(request)
    }
}
