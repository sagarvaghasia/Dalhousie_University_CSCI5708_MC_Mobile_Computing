open class Doctor(
    var name: String,
    var registry: Int,
    private var phoneNumber: String
) {
    // This function returns the value of phoneNumber.
    fun getphoneNumber(): String {
        return phoneNumber
    }

    /* This function sets the phoneNumber if the length of phoneNumber is 10
     and returns true else it does not set the phoneNumber and returns false. */
    fun setphoneNumber(phNumber: String) : Boolean {
        return if(phNumber.length == 10){
            phoneNumber = phNumber
            true
        }else{
            false
        }
    }

    // This function returns the name, registry and phoneNumber of a doctor.
    override fun toString(): String {
        return name + "(" + registry + ")" + "-" + phoneNumber
    }

}