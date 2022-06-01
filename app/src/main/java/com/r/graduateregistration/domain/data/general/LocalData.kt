package com.r.graduateregistration.domain.data.general

import com.r.graduateregistration.domain.models.Address
import com.r.graduateregistration.domain.models.University

class LocalData {
    companion object {

        val universityList = arrayListOf(
            University(
                "Select University",
                ""
            ),
            University(
                "Dr. Babasaheb Ambedkar Marathwada University, Aurangabad",
                "Aurangabad"
            ),
            University(
                "Swami Ramanand Teerth Marathwada University, Nanded",
                "Nanded"
            )
        )

        val address = arrayListOf(

            Address("Aurangabad", "Aurangabad"),
            Address("Aurangabad", "Sillod"),
            Address("Aurangabad", "Soegaon"),
            Address("Aurangabad", "Vaijapur"),
            Address("Aurangabad", "Gangapur"),
            Address("Aurangabad", "Paithan"),
            Address("Aurangabad", "Phulambri"),
            Address("Aurangabad", "Kannad"),
            Address("Aurangabad", "Khultabad"),
            Address("Osmanabad", "Osmanabad"),
            Address("Osmanabad", "Tuljapur"),
            Address("Osmanabad", "Umarga"),
            Address("Osmanabad", "Kalamb"),
            Address("Osmanabad", "Paranda"),
            Address("Osmanabad", "Bhum"),
            Address("Osmanabad", "Lohara"),
            Address("Osmanabad", "Washi"),
            Address("Jalna", "Jalna"),
            Address("Jalna", "Ambad"),
            Address("Jalna", "Bhokardan"),
            Address("Jalna", "Jafrabad"),
            Address("Jalna", "Partur"),
            Address("Jalna", "Ghansavngi"),
            Address("Jalna", "Mantha"),
            Address("Jalna", "Badnapur"),
            Address("Beed", "Beed"),
            Address("Beed", "Kaij"),
            Address("Beed", "Georai"),
            Address("Beed", "Patoda"),
            Address("Beed", "Ashti"),
            Address("Beed", "Shirur (Kasar)"),
            Address("Beed", "Ambajogai"),
            Address("Beed", "Kaij"),
            Address("Beed", "Majalgaon"),
            Address("Beed", "Dharur"),
            Address("Beed", "Parli (Vaijnath)"),
            Address("Beed", "Wadwani"),


        )



    }
}