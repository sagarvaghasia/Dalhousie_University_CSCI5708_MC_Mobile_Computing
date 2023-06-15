class HospitalDoctor(
    name: String,
    registry: Int,
    phoneNumber: String,
    var badgeNumber: String
) : Doctor(name, registry, phoneNumber), ICanOperate {

    // This function implements the method of ICanOperate and returns the string.
    override fun operateOnPatient(): String {
        return "Scalpel please"
    }

    // This function returns the name, registry, phoneNumber and badgeNumber of a hospital doctor.
     override fun toString(): String{
        return name + "(" + registry + ")" + "-" + super.getphoneNumber() + "\n" + "Badge: " + badgeNumber
    }
}