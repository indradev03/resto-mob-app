package com.example.indradev_resto.model

data class UserModel(
    var userId: String = "",
    var email: String = "",
    var password: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var selectedOptionText: String = "",
    var selectedDate: String = "",
    var selectedGender: String = "",
    var profileImageUrl: String = ""

)

