package com.example.uberdriver.data.remote.api.backend.authentication.model.request

data class CreateDriverRequest(val email:String,val firstName:String,val lastName:String,val country:String,val contactNumber:String){
    init {
        require(email.isNotBlank()) { "Email cannot be blank" }
        require(firstName.isNotBlank()) { "First name cannot be blank" }
        require(lastName.isNotBlank()) { "Last name cannot be blank" }
        require(contactNumber.isNotBlank()) { "Invalid contact number format" }
        require(country.isNotBlank()) { "Country cannot be blank" }
    }
}

data class CreateDriverReq(
    val driver:CreateDriverRequest
)