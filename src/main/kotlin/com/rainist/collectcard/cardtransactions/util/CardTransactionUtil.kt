package com.rainist.collectcard.cardtransactions.util

import com.rainist.collectcard.cardtransactions.dto.CardTransaction
import com.rainist.collectcard.common.db.entity.CardTransactionEntity
import java.time.LocalDateTime

class CardTransactionUtil {

    companion object {

        fun makeCardTransactionEntity(banksaladUserId: Long, organizationObjectId: String, cardTransaction: CardTransaction, now: LocalDateTime): CardTransactionEntity {
            return CardTransactionEntity().apply {
                this.banksaladUserId = banksaladUserId
                this.approvalYearMonth = cardTransaction.approvalDay?.substring(0, 6)
                this.cardCompanyId = organizationObjectId
                this.cardCompanyCardId = cardTransaction.cardCompanyCardId ?: ""
                this.approvalNumber = cardTransaction.approvalNumber
                this.approvalDay = cardTransaction.approvalDay
                this.approvalTime = cardTransaction.approvalTime
                this.cardName = cardTransaction.cardName
                this.cardNumber = cardTransaction.cardNumber
                this.cardNumberMask = cardTransaction.cardNumberMask
                this.amount = cardTransaction.amount
                this.canceledAmount = cardTransaction.canceledAmount
                this.partialCanceledAmount = cardTransaction.partialCanceledAmount
                this.tax = cardTransaction.tax
                this.serviceChargeAmount = cardTransaction.serviceChargeAmount
                this.netSalesAmount = cardTransaction.netSalesAmount
                this.businessLicenseNumber = cardTransaction.businessLicenseNumber
                this.storeName = cardTransaction.storeName
                this.storeNumber = cardTransaction.storeNumber
                this.storeCategory = cardTransaction.storeCategory
                this.cardType = cardTransaction.cardType.name
                this.cardTypeOrigin = cardTransaction.cardTypeOrigin
                this.cardTransactionType = cardTransaction.cardTransactionType.name
                this.cardTransactionTypeOrigin = cardTransaction.cardTransactionTypeOrigin
                this.currencyCode = cardTransaction.currencyCode ?: "KRW"
                this.transactionCountry = cardTransaction.transactionCountry
                this.isInstallmentPayment = cardTransaction.isInstallmentPayment ?: false
                this.installment = cardTransaction.installment ?: 0
                this.paymentDay = cardTransaction.paymentDay
                this.isOverseaUse = cardTransaction.isOverseaUse ?: false
                this.lastCheckAt = now
            }
        }

        // 임시.. 더 좋은방안이 있을지 확인 필요
        val currencyCodeMap: HashMap<String, String> = hashMapOf(
            // "아프가니" to "AFA",
            "레크" to "ALL",
            "알제리디나르" to "DZD",
            "안도라프랑" to "ADP",
            "앙골란뉴콴자" to "AON",
            "페소-아르헨티나" to "ARS",
            "호주달러" to "AUD",
            "오스트리안실링" to "ATS",
            "바하마달러" to "BSD",
            "바레인디나르" to "BHD",
            "타카" to "BDT",
            "아르메니안드람" to "AMD",
            "바베이도스달러" to "BBD",
            "벨기안프랑" to "BEF",
            "버뮤다달러" to "BMD",
            "볼리비아노" to "BOB",
            "풀라" to "BWP",
            "벨리즈달러" to "BZD",
            "달러-솔로몬아일랜드" to "SBD",
            "브루나이달러" to "BND",
            "레프" to "BGL",
            "차트" to "MMK",
            "브룬디프랑" to "BIF",
            // "벨라루시안루블" to "BYB",
            "리엘" to "KHR",
            "캐나다달러" to "CAD",
            "에스쿠도" to "CVE",
            "달러-케이만아일랜드" to "KYD",
            "스리랑카루피" to "LKR",
            "칠레페소" to "CLP",
            "위안" to "CNY",
            "콜롬비아페소" to "COP",
            "코모로프랑" to "KMF",
            "자이리언뉴자이레" to "ZRN",
            "콜론-코스타리카" to "CRC",
            "쿠나" to "HRK",
            "쿠바페소" to "CUP",
            "파운드-싸이프러스" to "CYP",
            "체코코루나" to "CZK",
            "덴마크크로네" to "DKK",
            "도미니칸페소" to "DOP",
            "에콰도르수크레" to "ECS",
            "콜론-엘살바도르" to "SVC",
            "비르" to "ETB",
            "EritranNakfa" to "ERN",
            "크룬" to "EEK",
            "파운드-포크랜드" to "FKP",
            "피지달러" to "FJD",
            "피니쉬마르카" to "FIM",
            "프렌치프랑" to "FRF",
            "지부티프랑" to "DJF",
            "달라시" to "GMD",
            "도이체마르크" to "DEM",
            "세디" to "GHC",
            "파운드-지브랄타" to "GIP",
            "그릭드락머" to "GRD",
            "케찰" to "GTQ",
            "기니프랑" to "GNF",
            "가이아나달러" to "GYD",
            "구르드" to "HTG",
            "렘피라" to "HNL",
            "홍콩달러" to "HKD",
            "포린트" to "HUF",
            "크로나-아이슬랜드" to "ISK",
            "인도루피" to "INR",
            "루피아" to "IDR",
            "이라니안리알" to "IRR",
            "이라크디나르" to "IQD",
            "아이리쉬파운드" to "IEP",
            "셰켈" to "ILS",
            "이탈리안리라" to "ITL",
            "자메이카달러" to "JMD",
            "엔" to "JPY",
            "텐지" to "KZT",
            "카자흐스탄" to "KZT",
            "요르단디나르" to "JOD",
            "케냐실링" to "KES",
            "북한원" to "KPW",
            "원" to "KRW",
            "디나르-쿠웨이트" to "KWD",
            "솜" to "KGS",
            "킵" to "LAK",
            "레바논파운드" to "LBP",
            "레소토로티" to "LSL",
            "라트" to "LVL",
            "달러-라이베리아" to "LRD",
            "리비안디나르" to "LYD",
            "리타스" to "LTL",
            "룩셈부르크프랑" to "LUF",
            "파타카" to "MOP",
            "콰차" to "MWK",
            "링기트" to "MYR",
            // "루피아" to "MVR",
            "말타리라" to "MTL",
            "오귀야" to "MRO",
            "모리셔스루피" to "MUR",
            "멕시코페소" to "MXN",
            "투그릭" to "MNT",
            "몰도반리우" to "MDL",
            "모로코디르함" to "MAD",
            //  "메티칼" to "MZM",
            "오만리알" to "OMR",
            "나미비아달러" to "NAD",
            "네팔루피" to "NPR",
            "네덜란드길더" to "NLG",
            "길더" to "ANG",
            "아루바플로린" to "AWG",
            "씨에프피프랑" to "NCL",
            "바투" to "VUV",
            "뉴질랜드달러" to "NZD",
            "코르도바" to "NIO",
            "나이라" to "NGN",
            "노르웨이크로네" to "NOK",
            "파키스탄루피" to "PKR",
            "발보아" to "PAB",
            "키나" to "PGK",
            "과라니" to "PYG",
            "누보솔" to "PEN",
            "필리핀페소" to "PHP",
            "포르투기즈에스쿠도" to "PTE",
            "페소" to "GWP",
            "포르투기즈티모리스에스쿠도" to "TPE",
            "카타르리알" to "QAR",
            "루블" to "RUB",
            "르완다프랑" to "RWF",
            "파운드-세인트헬레나" to "SHP",
            "도브라" to "STD",
            "사우디리알" to "SAR",
            "세이셸루피" to "SCR",
            "레오네" to "SLL",
            "싱가폴달러" to "SGD",
            "슬로박코루나" to "SKK",
            "동" to "VND",
            "슬로베니안톨라" to "SIT",
            "소말리아실링" to "SOS",
            "랜드" to "ZAR",
            "짐바브웨달러" to "ZWD",
            "스페니쉬페세타" to "ESP",
            "수다니즈파운드" to "SDD",
            "수리남길더" to "SRG",
            "릴랑게니" to "SZL",
            "스웨덴크로네" to "SEK",
            "스위스프랑" to "CHF",
            "시리아파운드" to "SYP",
            "타지키스탄루블" to "TJR",
            "바트" to "THB",
            "팡가" to "TOP",
            "달러-트리니다드" to "TTD",
            "디르함-아랍에미레이트" to "AED",
            "튀니지디나르" to "TND",
            "터키쉬리라" to "TRL",
            "마나트" to "TMM",
            "우간다실링" to "UGX",
            "마케도니아디나르" to "MKD",
            "러시안루블" to "RUR",
            "이집트파운드" to "EGP",
            "파운드" to "GBP",
            "탄자니아실링" to "TZS",
            "미국달러" to "USD",
            "우루과이페소" to "UYU",
            "우즈베키스탄섬" to "UZS",
            "볼리바" to "VEB",
            "탈라" to "WST",
            "예멘리알" to "YER",
            "세르비아디나르" to "CSD",
            // "콰차" to "ZMK",
            "대만달러" to "TWD",
            "벨라루시안루블" to "BYN",
            "마나트(투르크메니스탄)" to "TMT",
            "가나세디" to "GHS",
            "볼리바르푸에르테" to "VEF",
            "세르비안디나르" to "RSD",
            "메티칼" to "MZN",
            "아제르바이잔마나트" to "AZN",
            "뉴레우" to "RON",
            "뉴터키리라" to "TRY",
            "CFA프랑" to "XAF",
            "동카리브달러" to "XCD",
            "프랑" to "XOF",
            "CFP프랑" to "XPF",
            "잠비아콰차" to "ZMW",
            "수리남달러" to "SRD",
            "말라가시프랑" to "MGA",
            "마다가스카르아리아리" to "MGA",
            "마다가스카" to "MGA",
            "아프가니" to "AFN",
            "소모니" to "TJS",
            "콴자" to "AOA",
            // "벨라루시안루블" to "BYR",
            "불가리아레프" to "BGN",
            "콩고프랑" to "CDF",
            "컨버터블마크" to "BAM",
            "유로" to "EUR",
            "흐리브니아" to "UAH",
            "라리" to "GEL",
            "그루지아" to "GEL",
            "앙골라콴자" to "AOR",
            "즐로티" to "PLN",
            "레알" to "BRL",
            "미얀마키얏" to "MMK"
        )
    }
}
