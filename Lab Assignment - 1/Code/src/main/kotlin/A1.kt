fun main(args : Array<String>) {

    var doctor1 = Doctor("Sylvester Stallone", 5708, "9029894372")
    doctor1.setphoneNumber("1231231231")
    var doctor2 = Doctor("Jason Statham", 4176, "9106453796")
    doctor2.setphoneNumber("1231231231")
    var hospitalDoctor1 = HospitalDoctor("Dwayne Johnson", 6601, "8569754861", "HD1")
    hospitalDoctor1.setphoneNumber("1231231231")
    var hospitalDoctor2 = HospitalDoctor("Keanu Reeves", 8540, "7878654952", "HD2")
    hospitalDoctor2.setphoneNumber("1231231231")
    var hospitalDoctor3 = HospitalDoctor("Tom Cruise", 9999, "5656789423", "HD3")
    hospitalDoctor1.setphoneNumber("1231231231")

    val listOfDoctors = mutableListOf<Doctor>()
    listOfDoctors.add(doctor1)
    listOfDoctors.add(doctor2)
    listOfDoctors.add(hospitalDoctor1)
    listOfDoctors.add(hospitalDoctor2)
    listOfDoctors.add(hospitalDoctor3)

    for(d in listOfDoctors){
        println(d)
        if(d is HospitalDoctor){
            println(d.operateOnPatient())
        }
        print("\n")
    }

}