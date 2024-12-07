package `day-03-part-2`

import java.io.File

//I think this would be easier with regex but sounds fun to try to something different

enum class Token(val code: Char?) {
    M('m'),
    U('u'),
    L('l'),
    LPAREN('('),
    DIGIT(null),
    COMMA(','),
    RPAREN(')'),

    D('d'),
    O('o'),
    N('n'),
    QUOTE('\''),
    T('t'),
    UNTRACKED_TOKEN(null);

    companion object {
        // Lookup map for non-null characters
        private val charToTokenMap: Map<Char, Token> = entries
            .filter { it.code != null }
            .associateBy { it.code!! }

        fun fromChar(char: Char): Token? = charToTokenMap[char]
    }
}

enum class LexState {
    INITIAL_STATE,
    BUILDING_FIRST_WORD,
    BUILDING_FIRST_DIGIT,
    BUILDING_SECOND_DIGIT,
    TERMINAL_STATE
}

enum class DoLexState {
    INITIAL_STATE,
    BUILDING_WORD_BOTH_POSSIBLE,
    BUILDING_WORD_ONLY_DONT_POSSIBLE,
    BUILDING_WORD_ONLY_DO_POSSIBLE,
    TERMINAL_DO_STATE,
    TERMINAL_DONT_STATE
}

enum class DoState {
    DO,
    DONT
}

class StateMachine() {
    //initializing in terminal token
    var curToken: Token = Token.RPAREN;
    var curLexState: LexState = LexState.BUILDING_FIRST_WORD
    var curDoLexState: DoLexState = DoLexState.INITIAL_STATE
    var curDoState: DoState = DoState.DO
    var firstVal: String = "";
    var secondVal: String = "";

    var firstWordNextTokenMap: Map<Char, Char> =
        mapOf(
            'm' to 'u',
            'u' to 'l',
            'l' to '('
        )

    var dontWordNextTokenMap: Map<Char, Char> = mapOf(
        'd' to 'o',
        'o' to 'n',
        'n' to '\'',
        '\'' to 't',
        't' to '(',
        '(' to ')'
    )

    var doWordNextTokenMaps:  Map<Char, Char> = mapOf(
        'd' to 'o',
        'o' to '(',
        '(' to ')'
    )


    fun nextLexState(char: Char) {
        nextDoState(char)

        if (curLexState == LexState.TERMINAL_STATE) {
            curLexState = LexState.INITIAL_STATE
        }

        if (char == 'm') {
            curLexState = LexState.BUILDING_FIRST_WORD
            resetVals()
        }

        else if (Token.fromChar(char) == null && !char.isDigit()) {
            resetValsAndState()
        }

        else if (char in firstWordNextTokenMap.keys && curLexState == LexState.BUILDING_FIRST_WORD) {
            if (firstWordNextTokenMap[curToken.code] != char) {
                curLexState = LexState.INITIAL_STATE
        }
        }

        else if (char == '(' && curLexState == LexState.BUILDING_FIRST_WORD) {
            if (firstWordNextTokenMap[curToken.code] == '(') {
                curLexState = LexState.BUILDING_FIRST_DIGIT
            }
        }

        else if (char == ',') {
            if (curLexState == LexState.BUILDING_FIRST_DIGIT) {
                curLexState = LexState.BUILDING_SECOND_DIGIT
            }
            else {
                resetValsAndState()
            }
        }

        else if (char.isDigit()) {
            when (curLexState) {
                LexState.BUILDING_FIRST_WORD -> resetVals()
                LexState.BUILDING_FIRST_DIGIT -> firstVal += char
                LexState.BUILDING_SECOND_DIGIT -> secondVal += char
                LexState.TERMINAL_STATE -> resetVals()
                LexState.INITIAL_STATE -> {}
            }
        }

        else if (char == ')') {
            when (curLexState) {
                LexState.BUILDING_FIRST_WORD -> resetVals()
                LexState.BUILDING_FIRST_DIGIT -> resetVals()
                LexState.BUILDING_SECOND_DIGIT -> curLexState = LexState.TERMINAL_STATE
                LexState.TERMINAL_STATE -> resetVals()
                LexState.INITIAL_STATE -> {}
            }
        }

        curToken = Token.fromChar(char) ?: Token.UNTRACKED_TOKEN
    }


    private fun resetVals() {
        firstVal = "";
        secondVal = "";
    }

    private fun resetValsAndState() {
        resetVals()
        curLexState = LexState.INITIAL_STATE
    }

    private fun nextDoState(char: Char) {
        val nextValidDoToken = doWordNextTokenMaps[curToken.code]
        val nextValidDontToken = dontWordNextTokenMap[curToken.code]

        if (char == 'd') {
            curDoLexState = DoLexState.BUILDING_WORD_BOTH_POSSIBLE
        }

        else if (char == ')' && (curToken == Token.LPAREN)) {
            when (curDoLexState) {
                DoLexState.BUILDING_WORD_ONLY_DO_POSSIBLE -> {
                    curDoLexState = DoLexState.INITIAL_STATE
                    curDoState = DoState.DO
                }
                DoLexState.BUILDING_WORD_ONLY_DONT_POSSIBLE -> {
                    curDoLexState = DoLexState.INITIAL_STATE
                    curDoState = DoState.DONT
                }
                else -> {}
            }
        }

        else if (char !in doWordNextTokenMaps.keys && char !in dontWordNextTokenMap.keys) {
            curDoLexState = DoLexState.INITIAL_STATE
        }

        else if (nextValidDontToken != null && nextValidDontToken == char && nextValidDoToken == char) {
            curDoLexState = DoLexState.BUILDING_WORD_BOTH_POSSIBLE
        }

        else if (nextValidDontToken == char && nextValidDoToken != char && curDoLexState == DoLexState.BUILDING_WORD_BOTH_POSSIBLE) {
            curDoLexState = DoLexState.BUILDING_WORD_ONLY_DONT_POSSIBLE
        }

        else if (nextValidDontToken != char && nextValidDoToken == char && curDoLexState == DoLexState.BUILDING_WORD_BOTH_POSSIBLE) {
            curDoLexState = DoLexState.BUILDING_WORD_ONLY_DO_POSSIBLE
        }

        else if (nextValidDontToken != char && nextValidDoToken != char && curDoLexState !in listOf(DoLexState.TERMINAL_DO_STATE, DoLexState.TERMINAL_DONT_STATE)) {
            curDoLexState = DoLexState.INITIAL_STATE
        }

    }
}

fun main() {
    val preprocessedInput = File("src/day-03/input.txt")
        .readText()

    var result = 0;

    val stateMachine = StateMachine();

    preprocessedInput
        .forEach {
            stateMachine.nextLexState(it)
            if (it == ')' && stateMachine.curLexState == LexState.TERMINAL_STATE && stateMachine.curDoState == DoState.DO) {
                result += stateMachine.firstVal.toInt() * stateMachine.secondVal.toInt()
            }
        }

    println(result);
}

