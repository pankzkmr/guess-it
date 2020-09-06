package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import timber.log.Timber

// Global
// Buzz pattern
private val CORRECT_BUZZ_PATTERN = longArrayOf(0, 100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0, 1)

class GameViewModel : ViewModel() {

    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L
        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1000L
        // This is the total time of the game
        const val COUNTDOWN_TIME =  300000L
    }

    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

    private val timer: CountDownTimer

    // The current word
    private val _word = MutableLiveData<String>()
    val word : LiveData<String>
        get() = _word

    // The current score
    private val _score = MutableLiveData<Int>()
    val score : LiveData<Int>
        get() = _score

    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime

    val currentTimeString = Transformations.map(currentTime, { time ->
        DateUtils.formatElapsedTime(time)
    })

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish

    private val _eventBuzz = MutableLiveData<BuzzType>()
    val eventBuzz: LiveData<BuzzType>
        get() = _eventBuzz

    private val _noBuzzArray = NO_BUZZ_PATTERN
    val noBuzzArray: LongArray
        get() = _noBuzzArray

    init {
        Timber.i("GameViewModel created!")
        _eventGameFinish.value = false
//        _eventBuzz.value = null
        _eventBuzz.value = BuzzType.NO_BUZZ
        resetList()
        nextWord()
        _score.value = 0
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(p0: Long) {
                _currentTime.value = p0/1000
                val con = currentTime.value?.compareTo(10)
                if(con!! < 0) {
                    _eventBuzz.value = BuzzType.COUNTDOWN_PANIC
                }
            }
            override fun onFinish() {
                _eventGameFinish.value = true
                _eventBuzz.value = BuzzType.GAME_OVER
            }
        }
        timer.start()
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        Timber.i("GameViewModel Destroyed!")
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble",
                "jet lag",
                "facebook",
                "rock",
                "pillow",
                "wrench",
                "chop",
                "torch",
                "chickenpox",
                "cloud",
                "notepad",
                "android",
                "firefox",
                "eclipse",
                "lamp",
                "q-tips",
                "socket",
                "laptop",
                "keyboard",
                "pen-drive",
                "sketch",
                "elephant",
                "ocean",
                "book",
                "egg",
                "house",
                "dog",
                "ball",
                "star",
                "shirt",
                "ice",
                "cream",
                "christmas",
                "tree",
                "spider",
                "shoe",
                "smile",
                "cup",
                "hat",
                "bird",
                "kite",
                "butterfly",
                "cupcake",
                "fish",
                "grapes",
                "socks",
                "TV",
                "bed",
                "phone",
                "doll",
                "trash",
                "can",
                "sad",
                "airplane",
                "nose",
                "eyes",
                "apple",
                "sun",
                "bubble",
                "moon",
                "snow",
                "candy",
                "roof",
                "storm",
                "rice",
                "flag",
                "sand",
                "wing",
                "city",
                "farm",
                "watch",
                "garbage",
                "spring",
                "winter",
                "toaster",
                "jump",
                "dive",
                "sunglasses",
                "chef",
                "list",
                "book",
                "shelf",
                "hill",
                "pillowcase",
                "stop",
                "light",
                "teacher",
                "recess",
                "vacation",
                "police",
                "clock",
                "subway",
                "hair",
                "tie",
                "bag",
                "tent",
                "heaven",
                "gummy",
                "bears",
                "firefighter",
                "team",
                "morning",
                "dark",
                "pain",
                "homework",
                "glue",
                "eraser",
                "peace",
                "alarm",
                "far",
                "boring",
                "hot",
                "cold",
                "parents",
                "laugh",
                "hair",
                "ice",
                "afraid",
                "root",
                "beer",
                "float",
                "street",
                "sweeper",
                "drinking",
                "fountain",
                "imagination",
                "gum",
                "under",
                "the",
                "desk",
                "toilet",
                "paper",
                "fart",
                "communication",
                "magic"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
//            _eventGameFinish.value = true
            resetList()
        }
        _word.value = wordList.removeAt(0)
    }

    /** Methods for buttons presses **/

    fun onSkip() {
        _score.value = (score.value)?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = (score.value)?.plus(1)
        _eventBuzz.value = BuzzType.CORRECT
        nextWord()
    }

    fun onGameFinishComplete() {
        _eventGameFinish.value = false
    }

    fun onBuzzComplete() {
        _eventBuzz.value = BuzzType.NO_BUZZ
    }
}