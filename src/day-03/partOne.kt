package `day-03`

import java.io.File

//I think this would be easier with regex but sounds fun to try to something different

enum class Token(val code: Char?) {
    M('m'),
    U('u'),
    L('l'),
    LPAREN('('),
    DIGIT(null),
    COMMA(','),
    RPAREN(')');

    companion object {
        // Lookup map for non-null characters
        private val charToTokenMap: Map<Char, Token> = entries
            .filter { it.code != null }
            .associateBy { it.code!! }

        fun fromChar(char: Char): Token? = charToTokenMap[char]
    }
}

enum class State {
    BUILDING_FIRST_WORD,
    BUILDING_FIRST_DIGIT,
    BUILDING_SECOND_DIGIT,
    TERMINAL_STATE
}
class StateMachine() {
    //initializing in terminal token
    var curToken: Token = Token.RPAREN;
    var curState: State = State.BUILDING_FIRST_WORD
    var firstVal: String = "";
    var secondVal: String = "";

    var firstWordArray: List<Char> = listOf('u', 'l', '(');

    fun nextState(char: Char) {
        if (char == 'm') {
            curToken = Token.M
            resetVals()
        }

        else if (Token.fromChar(char) == null && !char.isDigit()) {
            curToken = Token.RPAREN
            resetVals()
        }

        else if (char in firstWordArray) {
            if (curToken.ordinal + 1 == Token.fromChar(char)!!.ordinal) {
                curToken = Token.fromChar(char)!!
                if ((curToken) == Token.LPAREN) {
                    curState = State.BUILDING_FIRST_DIGIT
                }
            }
        }

        else if (char == ',') {
            if (curState == State.BUILDING_FIRST_DIGIT) {
                curState = State.BUILDING_SECOND_DIGIT
                curToken = Token.COMMA
            }
        }

        else if (char.isDigit()) {
            when (curState) {
                State.BUILDING_FIRST_WORD -> resetVals()
                State.BUILDING_FIRST_DIGIT -> firstVal += char
                State.BUILDING_SECOND_DIGIT -> secondVal += char
                State.TERMINAL_STATE -> resetVals()
            }
        }

        else if (char == ')') {
            when (curState) {
                State.BUILDING_FIRST_WORD -> resetVals()
                State.BUILDING_FIRST_DIGIT -> resetVals()
                State.BUILDING_SECOND_DIGIT -> curState = State.TERMINAL_STATE
                State.TERMINAL_STATE -> resetVals()
            }
        }
    }

    private fun resetVals() {
        curState = State.BUILDING_FIRST_WORD;
        firstVal = "";
        secondVal = "";
    }
}

fun main() {
    val preprocessedInput = File("src/day-03/input.txt")
        .readText()

    var result = 0;

    val stateMachine = StateMachine();

    preprocessedInput
        .forEach {
            stateMachine.nextState(it)
            if (it == ')' && stateMachine.curState == State.TERMINAL_STATE ) {
                result += stateMachine.firstVal.toInt() * stateMachine.secondVal.toInt()
            }
        }

    println(result);
}

