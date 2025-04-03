interface SharedPrefInterface {
    fun getLanguage(): String
    fun setLanguage(value: String)

    fun getUnitSystem(): String
    fun setUnitSystem(value: String)

    fun getLocationSource(): String
    fun setLocationSource(value: String)

    fun getLat(): Double
    fun setLat(lat: Double)

    fun getLon(): Double
    fun setLon(lon: Double)

    fun getCity(): String
    fun setCity(city: String)
}