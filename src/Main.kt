fun main() {

    val hashTable = HashTableFactory.create()

    hashTable["emergency"] = "911"
    hashTable["Vlad"] = "88005553535"
    hashTable["Vlad"] = "88005553536"
    hashTable["Anna"] = "89999999999"
    hashTable["Disa"] = "89167214142"

    val emergencyPhone = hashTable["emergency"]
    val vladPhone = hashTable["Vlad"]
    val annaPhone = hashTable["Anna"]
    val disaPhone = hashTable["Disa"]

    println("emergency: $emergencyPhone")
    println("Vlad: $vladPhone")
    println("Anna: $annaPhone")
    println("Disa: $disaPhone")

    hashTable.remove("Anna")

    val annaPhoneAfterRemove = hashTable["Anna"]

    println("Anna: $annaPhoneAfterRemove")
}

/**
 * Контракт хэш-таблицы
 */
interface HashTable {

    /**
     * Получение значения по ключу
     *
     * @param key Ключ
     *
     * @return Возвращает значение по ключу, если оно существует, иначе null
     */
    operator fun get(key: String): String?

    /**
     * Получение значения по ключу
     *
     * @param key Ключ
     * @param value Значение
     */
    operator fun set(key: String, value: String)

    /**
     * Получение значения по ключу
     *
     * @param key Ключ
     *
     * @return Флаг об удавшемся удалении элемента
     */
    fun remove(key: String): Boolean
}

/**
 * Фабрика, создающая экземпляры хэш-таблицы
 */
object HashTableFactory {

    /**
     * Создает экземпляр хэш-таблицы
     */
    fun create(): HashTable {
        return HashTableImpl()
    }
}

/**
 * Реализация [HashTable]
 */
class HashTableImpl : HashTable {

    private companion object {
        const val ARRAY_SIZE_DEFAULT = 16
    }

    private val array: Array<HashTableNode?> = arrayOfNulls(ARRAY_SIZE_DEFAULT)

    override fun get(key: String): String? {
        val hash = countHash(key)
        val index = hash % ARRAY_SIZE_DEFAULT
        val node = array[index] ?: return null
        if (node.next == null) {
            return node.value
        }
        var current: HashTableNode? = node
        while (current != null) {
            if (current.key == key) {
                return current.value
            }
            current = current.next
        }
        return null
    }

    override fun set(key: String, value: String) {
        val hash = countHash(key)
        val index = hash % ARRAY_SIZE_DEFAULT
        val node = array[index]
        if (node == null) {
            array[index] = HashTableNode(key, value, null)
            return
        }
        var current = node
        while (true) {
            if (current?.key == key) {
                current.value = value
                return
            }
            if (current?.next == null) {
                current?.next = HashTableNode(key, value, null)
                return
            }
            current = current.next
        }
    }

    override fun remove(key: String): Boolean {
        val hash = countHash(key)
        val index = hash % ARRAY_SIZE_DEFAULT
        val node = array[index] ?: return false
        var previous: HashTableNode? = null
        var current: HashTableNode? = node
        while (current != null) {
            if (current.key == key) {
                when {
                    previous == null && current.next == null -> {
                        array[index] = null
                        return true
                    }

                    previous == null && current.next != null -> {
                        array[index] = current.next
                        return true
                    }

                    previous != null && current.next != null -> {
                        previous.next = current.next
                        return true
                    }

                    previous != null && current.next == null -> {
                        previous.next = null
                        return true
                    }
                }
            }
            previous = current
            current = current.next
        }
        return false
    }

    private fun countHash(key: String): Int {
        var sum = 0
        key.forEach { ch ->
            sum += ch.code
        }
        return sum
    }

    /**
     * Модель элемента хэш-таблицы
     *
     * @property key Ключ
     * @property value Значение
     * @property next Ссылка на слеющий элемент
     */
    private class HashTableNode(
        var key: String,
        var value: String,
        var next: HashTableNode?,
    )
}