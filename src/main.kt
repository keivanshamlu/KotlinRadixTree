

data class User(val name: String, val lastName: String)

fun main(){



    val exampleRadixTree = RadixTree<User>()

    fillWithFaker(exampleRadixTree)

    exampleRadixTree.getAllChildern().map {

        println(it)

    }

    println("""
        
        -------------
        
    """.trimIndent())

    exampleRadixTree.search("ab").map {

        println("${it.name} -- ${it.lastName}")

    }
    exampleRadixTree.getAllChildern().let { println(it.size) }
}

private fun fillWithFaker(exampleRadixTree : RadixTree<User>){

    val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    for (j in 5..15){
        for (i in 1..1_000){
            val randomString = (1..j)
                .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
            exampleRadixTree.insert(Item(randomString , User(randomString , "$randomString ${(0 until 1000).random()}")))
        }
    }
}
