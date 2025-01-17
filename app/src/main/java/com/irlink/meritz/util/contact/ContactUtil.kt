package com.irlink.meritz.util.contact

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract.CommonDataKinds.*
import android.provider.ContactsContract.Data
import android.provider.ContactsContract.Groups
import androidx.core.database.getLongOrNull
import com.irlink.meritz.util.FormatUtil
import com.irlink.meritz.util.RegexUtil
import android.provider.ContactsContract.Contacts as AndroidContacts

@SuppressLint("Recycle")
open class ContactUtil(

    private val applicationContext: Context,
    private val regexUtil: RegexUtil,
    private val formatUtil: FormatUtil

) {

    /**
     * 연락처 리스트를 검색하여 리턴.
     */
    open fun getContacts(): Contacts = Contacts().apply {
        val cursor: Cursor = applicationContext.contentResolver.query(
            Phone.CONTENT_URI,
            arrayOf(
                Data.CONTACT_ID,
                AndroidContacts.DISPLAY_NAME,
                Phone.TYPE,
                Phone.NUMBER
            ),
            null,
            null,
            Data.CONTACT_ID
        ) ?: return@apply

        val idColumnIndex: Int = cursor.getColumnIndex(Data.CONTACT_ID)
        val nameColumnIndex: Int = cursor.getColumnIndex(AndroidContacts.DISPLAY_NAME)
        val typeColumnIndex: Int = cursor.getColumnIndex(Phone.TYPE)
        val phoneNumberColumnIndex: Int = cursor.getColumnIndex(Phone.NUMBER)

        while (cursor.moveToNext()) {
            val id: Int = cursor.getInt(idColumnIndex)
            val type: Int = cursor.getInt(typeColumnIndex)
            val name: String = cursor.getString(nameColumnIndex)
            val phoneNumber: String = formatUtil.toLocalPhoneNumber(
                internationalNumber = cursor.getString(phoneNumberColumnIndex),
                includeHyphen = true
            )
            val contact: Contact = getById(id) ?: Contact(
                id = id,
                name = name
            ).also {
                add(it)
            }
            when (regexUtil.checkMobilePhoneNumber(phoneNumber)) {
                true -> contact.mobilePhone = phoneNumber
                else -> when (type) {
                    Phone.TYPE_HOME -> {
                        contact.homePhone = phoneNumber
                    }
                    Phone.TYPE_COMPANY_MAIN, Phone.TYPE_WORK -> {
                        contact.officePhone = phoneNumber
                    }
                    Phone.TYPE_FAX_HOME, Phone.TYPE_FAX_WORK, Phone.TYPE_OTHER_FAX -> {
                        contact.fax = phoneNumber
                    }
                }
            }
        }
        if (!cursor.isClosed) {
            cursor.close()
        }
        loadContactDetail(this)
    }

    /**
     * 연락처의 상세 정보를 추가함.
     */
    protected open fun loadContactDetail(contacts: Contacts) {
        val groups: Map<Long, String> = getGroups(applicationContext)

        val cursor: Cursor = applicationContext.contentResolver.query(
            Data.CONTENT_URI,
            arrayOf(
                Data.CONTACT_ID,
                StructuredPostal.TYPE,
                Data.MIMETYPE,
                Email.DATA,
                Note.NOTE,
                Organization.COMPANY,
                Organization.TITLE,
                Website.URL,
                Event.START_DATE,
                StructuredPostal.FORMATTED_ADDRESS,
                StructuredPostal.POBOX,
                GroupMembership.GROUP_ROW_ID
            ),
            null,
            null,
            Data.CONTACT_ID
        ) ?: return

        val idColumnIndex: Int = cursor.getColumnIndex(Data.CONTACT_ID)
        val typeColumnIndex: Int = cursor.getColumnIndex(StructuredPostal.TYPE)
        val mimeColumnIndex: Int = cursor.getColumnIndex(Data.MIMETYPE)
        val emailColumnIndex: Int = cursor.getColumnIndex(Email.DATA)
        val memoColumnIndex: Int = cursor.getColumnIndex(Note.NOTE)
        val companyColumnIndex: Int = cursor.getColumnIndex(Organization.COMPANY)
        val dutyColumnIndex: Int = cursor.getColumnIndex(Organization.TITLE)
        val siteColumnIndex: Int = cursor.getColumnIndex(Website.URL)
        val birthdayColumnIndex: Int = cursor.getColumnIndex(Event.START_DATE)
        val addressColumnIndex: Int = cursor.getColumnIndex(StructuredPostal.FORMATTED_ADDRESS)
        val zipColumnIndex: Int = cursor.getColumnIndex(StructuredPostal.POBOX)
        val groupNumberColumnIndex: Int = cursor.getColumnIndex(GroupMembership.GROUP_ROW_ID)

        while (cursor.moveToNext()) {
            val id: Int = cursor.getInt(idColumnIndex)
            val mime: String = cursor.getString(mimeColumnIndex)

            val contact: Contact = contacts.getById(id) ?: continue

            when (mime) {
                Email.CONTENT_ITEM_TYPE -> {
                    contact.email = cursor.getString(emailColumnIndex)
                }
                Note.CONTENT_ITEM_TYPE -> {
                    contact.memo = cursor.getString(memoColumnIndex)
                }
                Organization.CONTENT_ITEM_TYPE -> {
                    contact.company = cursor.getString(companyColumnIndex)
                    contact.duty = cursor.getString(dutyColumnIndex)
                }
                Website.CONTENT_ITEM_TYPE -> {
                    contact.homepage = cursor.getString(siteColumnIndex)
                }
                Event.CONTENT_ITEM_TYPE -> {
                    contact.birthday = cursor.getString(birthdayColumnIndex)
                }
                StructuredPostal.CONTENT_ITEM_TYPE -> {
                    val type: Int = cursor.getType(typeColumnIndex)
                    when (type) {
                        StructuredPostal.TYPE_HOME -> {
                            contact.homeAddress = cursor.getString(addressColumnIndex)
                            contact.homeZip = cursor.getString(zipColumnIndex)
                        }
                        StructuredPostal.TYPE_WORK -> {
                            contact.officeAddress = cursor.getString(addressColumnIndex)
                            contact.officeZip = cursor.getString(zipColumnIndex)
                        }
                    }
                }
                GroupMembership.CONTENT_ITEM_TYPE -> if (contact.groupNumber == null) {
                    val groupId: Long = cursor.getLong(groupNumberColumnIndex)
                    contact.groupNumber = groupId
                    contact.groupName = groups[groupId]
                }
            }
        }
        if (!cursor.isClosed) {
            cursor.close()
        }
    }

    /**
     * 연락처 그룹 목록을 맵 타입으로 리턴.
     * Key: GroupId
     * Value: GroupName
     */
    protected open fun getGroups(context: Context): Map<Long, String> =
        mutableMapOf<Long, String>().apply {
            val cursor: Cursor = context.contentResolver.query(
                Groups.CONTENT_SUMMARY_URI,
                arrayOf(
                    Groups._ID,
                    Groups.TITLE
                ),
                null,
                null,
                null
            ) ?: return@apply

            val idColumnIndex: Int = cursor.getColumnIndex(Groups._ID)
            val titleColumnIndex: Int = cursor.getColumnIndex(Groups.TITLE)

            while (cursor.moveToNext()) {
                val id: Long = cursor.getLongOrNull(idColumnIndex) ?: continue
                val title: String = cursor.getString(titleColumnIndex) ?: continue
                put(id, title)
            }
            if (!cursor.isClosed) {
                cursor.close()
            }
        }
}