package com.irlink.meritz.util.contact

open class Contacts : ArrayList<Contact>() {

    companion object {

        /**
         * 연락처 내 속성 간 구분자.
         */
        const val FIELD_SEPARATOR: Char = '^'

        /**
         * 연락처 간 구분자.
         */
        const val PHONE_BOOK_SEPARATOR: Char = '|'
    }

    /**
     * List 타입의 주소 아이템을 문자열 형식으로 리턴.
     */
    open val summary: String
        get() {
            var summary: String = ""

            for (contact: Contact in this) {
                if (contact.id != null) {
                    summary += contact.id
                }
                summary += FIELD_SEPARATOR
                if (!contact.name.isNullOrEmpty()) {
                    summary += contact.name
                }
                summary += FIELD_SEPARATOR
                if (!contact.mobilePhone.isNullOrEmpty()) {
                    summary += contact.mobilePhone
                }
                summary += FIELD_SEPARATOR
                if (!contact.homePhone.isNullOrEmpty()) {
                    summary += contact.homePhone
                }
                summary += FIELD_SEPARATOR
                if (!contact.officePhone.isNullOrEmpty()) {
                    summary += contact.officePhone
                }
                summary += FIELD_SEPARATOR
                if (!contact.fax.isNullOrEmpty()) {
                    summary += contact.fax
                }
                summary += FIELD_SEPARATOR
                if (!contact.email.isNullOrEmpty()) {
                    summary += contact.email
                }
                summary += FIELD_SEPARATOR
                if (!contact.company.isNullOrEmpty()) {
                    summary += contact.company
                }
                summary += FIELD_SEPARATOR
                if (!contact.homeZip.isNullOrEmpty()) {
                    summary += contact.homeZip
                }
                summary += FIELD_SEPARATOR
                if (!contact.homeAddress.isNullOrEmpty()) {
                    summary += contact.homeAddress
                }
                summary += FIELD_SEPARATOR
                if (!contact.officeZip.isNullOrEmpty()) {
                    summary += contact.officeZip
                }
                summary += FIELD_SEPARATOR
                if (!contact.officeAddress.isNullOrEmpty()) {
                    summary += contact.officeAddress
                }
                summary += FIELD_SEPARATOR
                if (contact.groupNumber != null) {
                    summary += contact.groupNumber
                }
                summary += FIELD_SEPARATOR
                if (!contact.groupName.isNullOrEmpty()) {
                    summary += contact.groupName
                }
                summary += FIELD_SEPARATOR
                if (!contact.homepage.isNullOrEmpty()) {
                    summary += contact.homepage
                }
                summary += FIELD_SEPARATOR
                if (!contact.birthday.isNullOrEmpty()) {
                    summary += contact.birthday
                }
                summary += FIELD_SEPARATOR
                if (!contact.duty.isNullOrEmpty()) {
                    summary += contact.duty
                }
                summary += FIELD_SEPARATOR
                if (!contact.memo.isNullOrEmpty()) {
                    summary += contact.memo
                }
                summary += FIELD_SEPARATOR
                summary += PHONE_BOOK_SEPARATOR
            }
            return summary
        }

    /**
     * 리스트 내에서 id로 연락처를 찾아서 리턴.
     */
    open fun getById(id: Int): Contact? {
        forEach { contact ->
            if (contact.id == id) return contact
        }
        return null
    }
}