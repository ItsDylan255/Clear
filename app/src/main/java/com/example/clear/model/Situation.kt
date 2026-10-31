package com.example.clear.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Waves
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.clear.tr

/** Die sechs Uebungskategorien. Sie bleiben bei jeder Emotion gleich. */
enum class ExerciseCategory(private val labelDe: String, private val labelEn: String) {
    Body("Körper", "Body"),
    Thoughts("Gedanken", "Thoughts"),
    Feelings("Gefühle", "Feelings"),
    Attention("Aufmerksamkeit", "Attention"),
    Action("Handlung", "Action"),
    SelfSupport("Selbstunterstützung", "Self-support");

    val label: String get() = tr(labelDe, labelEn)
}

/**
 * Eine Uebung. Es gibt keine feste Dauer, nichts ist verpflichtend und jede Uebung hat einen
 * klaren Endpunkt, der sich an der Durchfuehrung orientiert und nicht an einer Wirkung.
 */
data class Exercise(
    val id: String,
    val title: String,
    val category: ExerciseCategory,
    val goal: String,
    val steps: List<String>,
    val endpoint: String,
    val note: String? = null        // entlastender Zusatzsatz ("Du musst ...")
)

data class Situation(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val exercises: List<Exercise>,   // drei (siehe SHOWN_CATEGORIES)
    val intro: String? = null,       // ruhiger Satz oben auf der Seite
    val hint: String? = null         // Hinweiskarte (z. B. medizinische Hilfe)
)

/**
 * ALLE Inhalte des Akut-Bereichs stehen in dieser Datei.
 * Um Texte zu aendern, bearbeite die Aufrufe von ex(...) unten.
 */
object Situations {

    /**
     * Im Akut-Bereich erscheinen pro Emotion nur diese drei Kategorien. Die anderen Uebungen
     * bleiben unten im Code erhalten. Zum Aendern einfach die Auswahl hier anpassen.
     */
    private val SHOWN_CATEGORIES = setOf(
        ExerciseCategory.Body, ExerciseCategory.Attention, ExerciseCategory.SelfSupport
    )

    private fun shown(list: List<Exercise>) = list.filter { it.category in SHOWN_CATEGORIES }

    private fun ex(
        id: String,
        category: ExerciseCategory,
        title: String,
        goal: String,
        steps: List<String>,
        endpoint: String,
        note: String? = null
    ) = Exercise(id, title, category, goal, steps, endpoint, note)

    // ---------------------------------------------------------------- Angst
    private val anxiety: List<Exercise> get() = listOf(
        ex(
            "anxiety_body", ExerciseCategory.Body, tr("Anspannung loslassen", "Letting go of tension"),
            tr(
                "Körperliche Anspannung wahrnehmen und etwas lockern.",
                "Notice physical tension and loosen it a little."
            ),
            listOf(
                tr(
                    "Setze dich bequem hin oder stelle dich stabil hin.",
                    "Sit comfortably or stand in a steady position."
                ),
                tr(
                    "Bemerke, wo du Anspannung spürst, beispielsweise in Schultern, Händen oder Kiefer.",
                    "Notice where you feel tension, for example in your shoulders, hands or jaw."
                ),
                tr(
                    "Lass die Schultern sanft sinken und lockere deine Hände.",
                    "Gently let your shoulders drop and loosen your hands."
                ),
                tr("Spüre nach, ohne etwas erzwingen zu müssen.", "Notice how it feels, without forcing anything.")
            ),
            tr(
                "Fertig, sobald du die Anspannung wahrgenommen und Schultern und Hände gelockert hast.",
                "Done as soon as you have noticed the tension and loosened your shoulders and hands."
            ),
            tr(
                "Du musst dich nicht vollständig entspannen. Schon das Wahrnehmen reicht.",
                "You don't have to relax completely. Just noticing is enough."
            )
        ),
        ex(
            "anxiety_thoughts", ExerciseCategory.Thoughts, tr("Befürchtung oder Tatsache?", "Fear or fact?"),
            tr(
                "Angstgedanken betrachten, ohne sie automatisch für wahr zu halten.",
                "Look at anxious thoughts without automatically believing them."
            ),
            listOf(
                tr("Bemerke, wovor du gerade Angst hast.", "Notice what you are afraid of right now."),
                tr(
                    "Formuliere den Gedanken: „Ich befürchte, dass …“",
                    "Put the thought into words: “I'm afraid that …”"
                ),
                tr(
                    "Frage dich: „Was spricht dafür, was dagegen? Was weiß ich tatsächlich?“",
                    "Ask yourself: “What speaks for it, what speaks against it? What do I actually know?”"
                ),
                tr(
                    "Erinnere dich daran, dass du nicht jede Unsicherheit sofort auflösen musst.",
                    "Remind yourself that you don't have to resolve every uncertainty right away."
                )
            ),
            tr(
                "Fertig, sobald du zwischen Wissen und Befürchtung unterschieden hast.",
                "Done as soon as you have told apart what you know and what you fear."
            ),
            tr("Du musst keine eindeutige Antwort finden.", "You don't have to find a clear answer.")
        ),
        ex(
            "anxiety_feelings", ExerciseCategory.Feelings, tr("Der Angst Raum geben", "Making room for anxiety"),
            tr(
                "Angst anerkennen, ohne zusätzlich gegen sie anzukämpfen.",
                "Acknowledge anxiety without also fighting against it."
            ),
            listOf(
                tr("Sage dir innerlich: „Ich spüre gerade Angst.“", "Tell yourself: “I'm feeling anxious right now.”"),
                tr("Nimm wahr, wie sie sich anfühlt.", "Notice how it feels."),
                tr(
                    "Versuche, das Gefühl für einen Moment da sein zu lassen.",
                    "Try to let the feeling be there for a moment."
                ),
                tr(
                    "Erinnere dich: „Ich darf Angst haben und trotzdem Schritt für Schritt weitermachen.“",
                    "Remind yourself: “I'm allowed to be afraid and still keep going, step by step.”"
                )
            ),
            tr(
                "Fertig, sobald du deine Angst wahrgenommen und für einen Moment zugelassen hast.",
                "Done as soon as you have noticed your anxiety and let it be for a moment."
            ),
            tr(
                "Du musst die Angst weder mögen noch vollständig akzeptieren können.",
                "You don't have to like the anxiety or be able to fully accept it."
            )
        ),
        ex(
            "anxiety_attention", ExerciseCategory.Attention, tr(
                "Zurück ins Hier und Jetzt",
                "Back to the here and now"
            ),
            tr(
                "Die Aufmerksamkeit von angstauslösenden Gedanken auf die gegenwärtige Umgebung lenken.",
                "Shift your attention from anxious thoughts to your surroundings right now."
            ),
            listOf(
                tr("Schau dich langsam um.", "Slowly look around."),
                tr("Suche fünf Dinge, die du sehen kannst.", "Find five things you can see."),
                tr("Nimm drei Geräusche wahr.", "Notice three sounds."),
                tr(
                    "Spüre, wo dein Körper den Boden oder die Sitzfläche berührt.",
                    "Feel where your body touches the floor or the seat."
                ),
                tr(
                    "Richte deine Aufmerksamkeit wieder auf das, was du gerade wahrnimmst.",
                    "Bring your attention back to what you are perceiving right now."
                )
            ),
            tr(
                "Fertig, sobald du fünf Dinge, drei Geräusche und den Kontakt zum Boden wahrgenommen hast.",
                "Done as soon as you have noticed five things, three sounds and your contact with the floor."
            )
        ),
        ex(
            "anxiety_action", ExerciseCategory.Action, tr("Ein kleiner Schritt", "One small step"),
            tr("Trotz Angst wieder etwas Handlungsspielraum gewinnen.", "Regain some room to act despite the anxiety."),
            listOf(
                tr(
                    "Überlege, welche Situation dich gerade beschäftigt.",
                    "Think about which situation is on your mind right now."
                ),
                tr(
                    "Frage dich: „Was wäre ein kleiner, sicherer Schritt, der mir jetzt helfen könnte?“",
                    "Ask yourself: “What would be a small, safe step that could help me now?”"
                ),
                tr(
                    "Wähle eine überschaubare Handlung, zum Beispiel eine Nachricht schreiben, ein Glas Wasser holen oder eine Aufgabe in einem kleinen Teil beginnen.",
                    "Choose a manageable action, for example writing a message, getting a glass of water or starting a small part of a task."
                ),
                tr("Konzentriere dich nur auf diesen Schritt.", "Focus only on this step.")
            ),
            tr(
                "Fertig, sobald du einen kleinen, sicheren Schritt gewählt oder dich für eine Pause entschieden hast.",
                "Done as soon as you have chosen a small, safe step or decided to take a break."
            ),
            tr("Du musst nicht die gesamte Situation lösen.", "You don't have to solve the whole situation.")
        ),
        ex(
            "anxiety_support", ExerciseCategory.SelfSupport, tr("Mein eigener Halt", "My own anchor"),
            tr(
                "In einem schwierigen Moment einen hilfreichen inneren Anker finden.",
                "Find a helpful inner anchor in a difficult moment."
            ),
            listOf(
                tr(
                    "Stell dir vor, ein guter Freund hätte gerade genau diese Angst.",
                    "Imagine a good friend had exactly this fear right now."
                ),
                tr(
                    "Überlege, was du ihm verständnisvoll sagen würdest.",
                    "Think about what you would kindly say to them."
                ),
                tr(
                    "Wähle einen Satz, der sich für dich passend anfühlt.",
                    "Choose a sentence that feels right for you."
                ),
                tr(
                    "Wiederhole ihn langsam, beispielsweise: „Ich muss das gerade nicht alles auf einmal lösen.“",
                    "Repeat it slowly, for example: “I don't have to solve all of this at once right now.”"
                )
            ),
            tr(
                "Fertig, sobald du einen hilfreichen Satz für dich gefunden hast.",
                "Done as soon as you have found a helpful sentence for yourself."
            )
        )
    )

    // ---------------------------------------------------------------- Panik
    private val panic: List<Exercise> get() = listOf(
        ex(
            "panic_body", ExerciseCategory.Body, tr("Eine bequeme Position finden", "Finding a comfortable position"),
            tr(
                "Dem Körper Halt geben, ohne ihn zu einer bestimmten Reaktion zu zwingen.",
                "Give your body support without forcing it to react in a certain way."
            ),
            listOf(
                tr(
                    "Setze dich hin, wenn das für dich sicher und angenehm ist. Alternativ kannst du stehen bleiben und dich an etwas Stabilem orientieren.",
                    "Sit down if that feels safe and comfortable. Or stay standing and hold on to something steady."
                ),
                tr(
                    "Lass deine Schultern locker, soweit es sich angenehm anfühlt.",
                    "Let your shoulders loosen as far as feels comfortable."
                ),
                tr("Lege deine Hände bequem ab.", "Rest your hands comfortably."),
                tr(
                    "Atme so, wie es gerade von selbst kommt. Du musst deinen Atem nicht vertiefen oder kontrollieren.",
                    "Breathe the way it comes naturally right now. You don't have to deepen or control your breath."
                ),
                tr(
                    "Prüfe, ob deine Position angenehm genug ist, um einen Moment darin zu bleiben.",
                    "Check whether your position is comfortable enough to stay in it for a moment."
                )
            ),
            tr(
                "Fertig, sobald du eine möglichst bequeme, sichere Position gefunden hast.",
                "Done as soon as you have found a position that is as comfortable and safe as possible."
            ),
            tr(
                "Du musst dich nicht entspannen oder körperliche Symptome zum Verschwinden bringen.",
                "You don't have to relax or make physical symptoms go away."
            )
        ),
        ex(
            "panic_thoughts", ExerciseCategory.Thoughts, tr("Panikgedanken einordnen", "Sorting panic thoughts"),
            tr(
                "Beängstigende Gedanken wahrnehmen, ohne sie automatisch als Tatsachen zu behandeln.",
                "Notice frightening thoughts without automatically treating them as facts."
            ),
            listOf(
                tr(
                    "Bemerke, welcher Gedanke dir gerade besonders viel Angst macht.",
                    "Notice which thought is scaring you most right now."
                ),
                tr(
                    "Benenne ihn innerlich: „Ich habe gerade den Gedanken, dass …“",
                    "Name it to yourself: “I'm having the thought that …”"
                ),
                tr(
                    "Erinnere dich daran, dass ein Gedanke zunächst eine Vorstellung oder Befürchtung ist.",
                    "Remind yourself that a thought is first of all an idea or a fear."
                ),
                tr("Versuche nicht, alle Zweifel sofort aufzulösen.", "Don't try to clear up every doubt right away."),
                tr(
                    "Richte deine Aufmerksamkeit auf das, was du im Moment tatsächlich weißt.",
                    "Turn your attention to what you actually know right now."
                )
            ),
            tr(
                "Fertig, sobald du einen beängstigenden Gedanken als Gedanken erkannt hast.",
                "Done as soon as you have recognized a frightening thought as a thought."
            ),
            tr(
                "Du musst dich nicht davon überzeugen, dass alles in Ordnung ist.",
                "You don't have to convince yourself that everything is fine."
            )
        ),
        ex(
            "panic_feelings", ExerciseCategory.Feelings, tr("Den Moment anerkennen", "Acknowledging the moment"),
            tr(
                "Die eigene Reaktion wahrnehmen, ohne sich zusätzlich dafür zu verurteilen.",
                "Notice your own reaction without also judging yourself for it."
            ),
            listOf(
                tr(
                    "Sage dir innerlich: „Ich erlebe gerade starke Angst.“",
                    "Tell yourself: “I'm experiencing strong fear right now.”"
                ),
                tr(
                    "Nimm wahr, was gerade in dir vorgeht, ohne jedes Gefühl genau analysieren zu müssen.",
                    "Notice what is going on inside you without having to analyze every feeling."
                ),
                tr("Erlaube dir, dass sich dieser Moment schwierig anfühlt.", "Allow this moment to feel difficult."),
                tr(
                    "Sage dir, wenn es für dich passend ist: „Ich muss dieses Gefühl nicht sofort verändern.“",
                    "If it feels right, tell yourself: “I don't have to change this feeling right away.”"
                ),
                tr("Lass offen, wie lange das Gefühl noch anhält.", "Leave open how long the feeling will last.")
            ),
            tr(
                "Fertig, sobald du anerkannt hast, dass du gerade starke Angst erlebst.",
                "Done as soon as you have acknowledged that you are experiencing strong fear right now."
            ),
            tr("Die Panik muss dafür nicht nachlassen.", "The panic doesn't have to ease for this.")
        ),
        ex(
            "panic_attention", ExerciseCategory.Attention, tr(
                "Einen festen Orientierungspunkt finden",
                "Finding a fixed point"
            ),
            tr(
                "Einen überschaubaren Bezugspunkt in der gegenwärtigen Umgebung finden.",
                "Find a simple point of reference in your surroundings right now."
            ),
            listOf(
                tr("Suche dir einen Gegenstand in deiner Umgebung aus.", "Pick an object around you."),
                tr("Schau dir seine Form und Farbe an.", "Look at its shape and color."),
                tr("Bemerke, wo sich der Gegenstand befindet.", "Notice where the object is."),
                tr(
                    "Wenn es angenehm ist, nimm zusätzlich ein Geräusch aus deiner Umgebung wahr.",
                    "If it feels okay, also notice a sound around you."
                ),
                tr(
                    "Kehre mit deiner Aufmerksamkeit zum Gegenstand zurück, wenn deine Gedanken abschweifen.",
                    "Bring your attention back to the object when your thoughts wander."
                )
            ),
            tr(
                "Fertig, sobald du einen Gegenstand betrachtet und ein Merkmal daran bemerkt hast.",
                "Done as soon as you have looked at an object and noticed one detail about it."
            ),
            tr(
                "Du musst dich nicht vollständig auf deine Umgebung konzentrieren können.",
                "You don't have to be able to focus fully on your surroundings."
            )
        ),
        ex(
            "panic_action", ExerciseCategory.Action, tr(
                "Den nächsten sicheren Schritt wählen",
                "Choosing the next safe step"
            ),
            tr(
                "In einer überwältigenden Situation eine einfache und sichere Handlung finden.",
                "Find a simple and safe action in an overwhelming situation."
            ),
            listOf(
                tr("Frage dich: „Was brauche ich gerade unmittelbar?“", "Ask yourself: “What do I need right now?”"),
                tr(
                    "Überlege, ob du dich hinsetzen, an einen ruhigeren Ort gehen oder eine vertraute Person kontaktieren möchtest.",
                    "Think about whether you would like to sit down, go somewhere quieter or contact someone you trust."
                ),
                tr("Wähle nur einen nächsten Schritt.", "Choose just one next step."),
                tr("Setze ihn um, wenn er sicher und machbar ist.", "Take it if it is safe and doable."),
                tr(
                    "Wenn du gerade nichts tun kannst, darfst du zunächst an einem sicheren Ort bleiben.",
                    "If you can't do anything right now, it's okay to stay in a safe place for now."
                )
            ),
            tr(
                "Fertig, sobald du einen passenden nächsten Schritt gewählt hast.",
                "Done as soon as you have chosen a suitable next step."
            ),
            tr("Du musst nicht die gesamte Situation bewältigen.", "You don't have to handle the whole situation.")
        ),
        ex(
            "panic_support", ExerciseCategory.SelfSupport, tr(
                "Ich muss das nicht allein bewältigen",
                "I don't have to handle this alone"
            ),
            tr(
                "Sich selbst Halt geben und bei Bedarf Unterstützung einbeziehen.",
                "Support yourself and bring in help if you need it."
            ),
            listOf(
                tr(
                    "Erinnere dich daran, dass du in diesem Moment Unterstützung verdienen darfst.",
                    "Remind yourself that you deserve support in this moment."
                ),
                tr(
                    "Wähle einen Satz, der sich für dich hilfreich anfühlt, beispielsweise: „Ich darf mir jetzt Hilfe holen.“",
                    "Choose a sentence that feels helpful, for example: “It's okay to get help now.”"
                ),
                tr("Wenn du möchtest, kontaktiere eine vertraute Person.", "If you like, contact someone you trust."),
                tr(
                    "Du kannst ausdrücklich sagen, was du brauchst, etwa: „Kannst du kurz bei mir bleiben?“",
                    "You can say clearly what you need, for example: “Can you stay with me for a bit?”"
                ),
                tr(
                    "Falls du lieber allein bleiben möchtest, überlege, was dir hilft, dich an einem sicheren Ort zu orientieren.",
                    "If you would rather be alone, think about what helps you find your bearings in a safe place."
                )
            ),
            tr(
                "Fertig, sobald du einen unterstützenden Satz gefunden oder entschieden hast, welche Unterstützung du möchtest.",
                "Done as soon as you have found a supportive sentence or decided what kind of support you want."
            ),
            tr(
                "Du musst niemanden kontaktieren, wenn du das gerade nicht möchtest und keine unmittelbare Hilfe benötigst.",
                "You don't have to contact anyone if you don't want to right now and don't need immediate help."
            )
        )
    )

    // ---------------------------------------------------------------- Wut
    private val anger: List<Exercise> get() = listOf(
        ex(
            "anger_body", ExerciseCategory.Body, tr("Körperliche Anspannung wahrnehmen", "Noticing physical tension"),
            tr(
                "Anspannung bemerken und einen sicheren Umgang damit finden, ohne impulsiv handeln zu müssen.",
                "Notice tension and find a safe way to deal with it without acting on impulse."
            ),
            listOf(
                tr("Nimm wahr, wie sich dein Körper gerade anfühlt.", "Notice how your body feels right now."),
                tr(
                    "Achte besonders auf Kiefer, Hände, Schultern und Bauch.",
                    "Pay special attention to your jaw, hands, shoulders and stomach."
                ),
                tr(
                    "Lockere deine Hände und lass deine Schultern sinken, wenn es angenehm ist.",
                    "Loosen your hands and let your shoulders drop if that feels good."
                ),
                tr(
                    "Wenn du Bewegungsdrang verspürst, gehe ein paar Schritte oder bewege dich auf eine Weise, die dir und anderen nicht schadet.",
                    "If you feel the urge to move, walk a few steps or move in a way that doesn't harm you or others."
                ),
                tr("Du musst die Wut nicht wegdrücken.", "You don't have to push the anger away.")
            ),
            tr(
                "Fertig, sobald du eine körperliche Empfindung wahrgenommen und einen Umgang mit der Anspannung gefunden hast.",
                "Done as soon as you have noticed a physical sensation and found a way to deal with the tension."
            )
        ),
        ex(
            "anger_thoughts", ExerciseCategory.Thoughts, tr("Was hat mich wütend gemacht?", "What made me angry?"),
            tr(
                "Den Auslöser und die eigene Interpretation unterscheiden, ohne die eigenen Gefühle abzuwerten.",
                "Tell apart the trigger and your own interpretation without dismissing your feelings."
            ),
            listOf(
                tr(
                    "Frage dich: „Welche Situation hat meine Wut ausgelöst?“",
                    "Ask yourself: “Which situation triggered my anger?”"
                ),
                tr(
                    "Unterscheide zwischen dem, was tatsächlich passiert ist, und dem, was du darin vermutest.",
                    "Separate what actually happened from what you assume about it."
                ),
                tr(
                    "Überlege, welche Erwartung, Grenze oder welches Bedürfnis möglicherweise verletzt wurde.",
                    "Think about which expectation, boundary or need may have been hurt."
                ),
                tr(
                    "Erinnere dich daran, dass du nicht sofort entscheiden musst, wer recht hat oder was du tun wirst.",
                    "Remind yourself that you don't have to decide right away who is right or what you will do."
                )
            ),
            tr(
                "Fertig, sobald du den Auslöser benannt und einen möglichen Gedanken oder eine verletzte Grenze erkannt hast.",
                "Done as soon as you have named the trigger and recognized a possible thought or a crossed boundary."
            ),
            tr(
                "Du musst die Situation nicht abschließend bewerten.",
                "You don't have to come to a final judgment about the situation."
            )
        ),
        ex(
            "anger_feelings", ExerciseCategory.Feelings, tr("Wut anerkennen", "Acknowledging anger"),
            tr(
                "Wut zulassen, ohne sie zu verurteilen oder automatisch nach ihr zu handeln.",
                "Allow anger without judging it or automatically acting on it."
            ),
            listOf(
                tr("Sage dir innerlich: „Ich bin gerade wütend.“", "Tell yourself: “I'm angry right now.”"),
                tr(
                    "Nimm wahr, wie stark sich die Wut anfühlt, ohne sie genau messen zu müssen.",
                    "Notice how strong the anger feels without having to measure it exactly."
                ),
                tr(
                    "Erinnere dich daran, dass ein Gefühl nicht dasselbe ist wie eine Handlung.",
                    "Remind yourself that a feeling is not the same as an action."
                ),
                tr(
                    "Erlaube dir, wütend zu sein, ohne sofort reagieren zu müssen.",
                    "Allow yourself to be angry without having to react right away."
                ),
                tr(
                    "Falls du gerade Abstand brauchst, darfst du ihn dir nehmen.",
                    "If you need some distance right now, it's okay to take it."
                )
            ),
            tr(
                "Fertig, sobald du deine Wut als Gefühl anerkannt hast.",
                "Done as soon as you have acknowledged your anger as a feeling."
            ),
            tr("Die Wut muss dafür nicht verschwunden sein.", "The anger doesn't have to be gone for this.")
        ),
        ex(
            "anger_attention", ExerciseCategory.Attention, tr(
                "Den Fokus bewusst wechseln",
                "Deliberately shifting focus"
            ),
            tr(
                "Für einen Moment Abstand von der Situation gewinnen, die die Wut verstärkt.",
                "Gain some distance for a moment from the situation that fuels the anger."
            ),
            listOf(
                tr(
                    "Richte deine Aufmerksamkeit auf einen Gegenstand in deiner Umgebung.",
                    "Turn your attention to an object around you."
                ),
                tr("Nimm seine Form, Farbe und Oberfläche wahr.", "Notice its shape, color and surface."),
                tr("Höre auf ein Geräusch in deiner Umgebung.", "Listen to a sound around you."),
                tr("Spüre den Kontakt deiner Füße mit dem Boden.", "Feel your feet touching the floor."),
                tr(
                    "Wenn deine Gedanken zur Situation zurückkehren, lenke deine Aufmerksamkeit erneut auf deine Umgebung.",
                    "When your thoughts return to the situation, bring your attention back to your surroundings."
                )
            ),
            tr(
                "Fertig, sobald du einen Gegenstand und ein weiteres Sinnesdetail wahrgenommen hast.",
                "Done as soon as you have noticed an object and one more sensory detail."
            ),
            tr(
                "Du musst die Situation nicht vergessen oder deine Wut verlieren.",
                "You don't have to forget the situation or lose your anger."
            )
        ),
        ex(
            "anger_action", ExerciseCategory.Action, tr(
                "Eine hilfreiche Reaktion wählen",
                "Choosing a helpful response"
            ),
            tr(
                "Einen sicheren und bewussten Umgang mit der Situation finden, statt impulsiv zu reagieren.",
                "Find a safe and deliberate way to deal with the situation instead of reacting on impulse."
            ),
            listOf(
                tr(
                    "Frage dich: „Muss ich gerade etwas tun, oder kann ich mir zunächst Abstand nehmen?“",
                    "Ask yourself: “Do I have to do something right now, or can I take some distance first?”"
                ),
                tr(
                    "Wenn du sehr aufgebracht bist, verschiebe ein schwieriges Gespräch, sofern das möglich ist.",
                    "If you are very upset, postpone a difficult conversation if you can."
                ),
                tr(
                    "Überlege, was du eigentlich erreichen möchtest: eine Grenze setzen, etwas klären oder eine Veränderung ansprechen.",
                    "Think about what you actually want to achieve: setting a boundary, clearing something up or asking for a change."
                ),
                tr(
                    "Wähle einen nächsten Schritt, der dich diesem Ziel näherbringt, ohne dir oder anderen zu schaden.",
                    "Choose a next step that brings you closer to this goal without harming you or others."
                ),
                tr(
                    "Wenn noch keine passende Reaktion möglich ist, darfst du die Entscheidung vertagen.",
                    "If no suitable response is possible yet, it's okay to put off the decision."
                )
            ),
            tr(
                "Fertig, sobald du einen sicheren nächsten Schritt gewählt oder entschieden hast, vorerst nicht zu reagieren.",
                "Done as soon as you have chosen a safe next step or decided not to react for now."
            ),
            tr("Du musst den Konflikt nicht sofort lösen.", "You don't have to resolve the conflict right away.")
        ),
        ex(
            "anger_support", ExerciseCategory.SelfSupport, tr("Meine Wut ernst nehmen", "Taking my anger seriously"),
            tr(
                "Die eigenen Bedürfnisse und Grenzen anerkennen, ohne sich selbst oder andere abzuwerten.",
                "Acknowledge your own needs and boundaries without putting yourself or others down."
            ),
            listOf(
                tr("Frage dich: „Was brauche ich gerade?“", "Ask yourself: “What do I need right now?”"),
                tr(
                    "Überlege, ob du Abstand, Ruhe, Verständnis, Fairness oder eine klare Grenze brauchst.",
                    "Think about whether you need distance, quiet, understanding, fairness or a clear boundary."
                ),
                tr(
                    "Formuliere einen Satz, der dich unterstützt, beispielsweise: „Meine Gefühle sind wichtig, und ich darf entscheiden, wie ich damit umgehe.“",
                    "Put a supportive sentence into words, for example: “My feelings matter, and I get to decide how I deal with them.”"
                ),
                tr(
                    "Wenn eine Grenze verletzt wurde, überlege, wie du sie später ruhig und klar ausdrücken kannst.",
                    "If a boundary was crossed, think about how you could express it calmly and clearly later."
                ),
                tr(
                    "Erinnere dich daran, dass du nicht sofort eine perfekte Lösung finden musst.",
                    "Remind yourself that you don't have to find a perfect solution right away."
                )
            ),
            tr(
                "Fertig, sobald du ein Bedürfnis oder eine Grenze erkannt und einen unterstützenden Satz gefunden hast.",
                "Done as soon as you have recognized a need or a boundary and found a supportive sentence."
            ),
            tr(
                "Du musst noch nichts aussprechen oder mit jemandem klären.",
                "You don't have to say anything or sort anything out with anyone yet."
            )
        )
    )

    // ---------------------------------------------------------------- Grübeln
    private val rumination: List<Exercise> get() = listOf(
        ex(
            "rumination_body", ExerciseCategory.Body, tr(
                "Aus dem Kopf in den Körper",
                "Out of your head, into your body"
            ),
            tr(
                "Die Aufmerksamkeit wieder auf körperliche Empfindungen richten, wenn sich Gedanken ständig wiederholen.",
                "Bring your attention back to physical sensations when your thoughts keep repeating."
            ),
            listOf(
                tr("Nimm eine bequeme Position ein.", "Get into a comfortable position."),
                tr("Spüre, wie deine Füße den Boden berühren.", "Feel your feet touching the floor."),
                tr(
                    "Bemerke, welche Körperbereiche du gerade wahrnehmen kannst.",
                    "Notice which parts of your body you can feel right now."
                ),
                tr(
                    "Wenn du möchtest, bewege langsam deine Hände oder Schultern.",
                    "If you like, slowly move your hands or shoulders."
                ),
                tr(
                    "Richte deine Aufmerksamkeit immer wieder auf die körperliche Wahrnehmung, wenn du bemerkst, dass du erneut ins Grübeln gerätst.",
                    "Keep bringing your attention back to your body whenever you notice yourself ruminating again."
                )
            ),
            tr(
                "Fertig, sobald du eine körperliche Empfindung wahrgenommen hast.",
                "Done as soon as you have noticed a physical sensation."
            ),
            tr("Du musst deine Gedanken nicht vollständig stoppen.", "You don't have to stop your thoughts completely.")
        ),
        ex(
            "rumination_thoughts", ExerciseCategory.Thoughts, tr(
                "Kann ich das gerade lösen?",
                "Can I solve this right now?"
            ),
            tr(
                "Zwischen hilfreichem Nachdenken und wiederkehrenden Gedankenschleifen unterscheiden.",
                "Tell apart helpful thinking and repetitive thought loops."
            ),
            listOf(
                tr("Bemerke, welcher Gedanke immer wieder auftaucht.", "Notice which thought keeps coming back."),
                tr(
                    "Frage dich: „Gibt es gerade etwas Konkretes, das ich an dieser Situation verändern kann?“",
                    "Ask yourself: “Is there something concrete I can change about this situation right now?”"
                ),
                tr(
                    "Wenn ja, überlege, welcher kleine nächste Schritt möglich wäre.",
                    "If yes, think about which small next step would be possible."
                ),
                tr(
                    "Wenn nein, erkenne an, dass weiteres Nachdenken möglicherweise gerade keine neue Antwort liefert.",
                    "If not, accept that more thinking may not bring a new answer right now."
                ),
                tr(
                    "Erinnere dich daran, dass du eine Frage auch offenlassen darfst.",
                    "Remind yourself that it's okay to leave a question open."
                )
            ),
            tr(
                "Fertig, sobald du eingeordnet hast, ob du etwas lösen kannst oder die Frage offenlässt.",
                "Done as soon as you have decided whether you can solve something or leave the question open."
            ),
            tr("Du musst keine endgültige Antwort finden.", "You don't have to find a final answer.")
        ),
        ex(
            "rumination_feelings", ExerciseCategory.Feelings, tr(
                "Was steckt hinter dem Grübeln?",
                "What's behind the rumination?"
            ),
            tr(
                "Wahrnehmen, welche Gefühle mit den wiederkehrenden Gedanken verbunden sein könnten.",
                "Notice which feelings might be connected to the recurring thoughts."
            ),
            listOf(
                tr("Bemerke, worüber du immer wieder nachdenkst.", "Notice what you keep thinking about."),
                tr(
                    "Frage dich: „Was fühle ich, wenn ich an dieses Thema denke?“",
                    "Ask yourself: “What do I feel when I think about this?”"
                ),
                tr(
                    "Vielleicht bemerkst du Angst, Schuld, Traurigkeit, Wut oder Unsicherheit.",
                    "Maybe you notice fear, guilt, sadness, anger or uncertainty."
                ),
                tr(
                    "Versuche, das Gefühl zu benennen, ohne es sofort erklären oder beseitigen zu müssen.",
                    "Try to name the feeling without having to explain it or get rid of it right away."
                ),
                tr(
                    "Erlaube dir, das Gefühl wahrzunehmen, auch wenn die zugrunde liegende Frage ungeklärt bleibt.",
                    "Allow yourself to notice the feeling, even if the underlying question stays unresolved."
                )
            ),
            tr(
                "Fertig, sobald du ein mögliches Gefühl hinter deinen Gedanken erkannt oder gemerkt hast, dass du es nicht benennen kannst.",
                "Done as soon as you have recognized a possible feeling behind your thoughts or noticed that you can't name it."
            )
        ),
        ex(
            "rumination_attention", ExerciseCategory.Attention, tr(
                "Einen anderen Fokus finden",
                "Finding a different focus"
            ),
            tr(
                "Die Aufmerksamkeit bewusst aus der Gedankenschleife auf etwas Gegenwärtiges lenken.",
                "Deliberately move your attention out of the thought loop to something in the present."
            ),
            listOf(
                tr(
                    "Suche dir etwas in deiner Umgebung aus, auf das du deine Aufmerksamkeit richten möchtest.",
                    "Pick something around you that you would like to focus on."
                ),
                tr(
                    "Beobachte beispielsweise die Bewegungen eines Baumes, ein Muster oder das Licht im Raum.",
                    "For example, watch the movement of a tree, a pattern or the light in the room."
                ),
                tr(
                    "Nimm möglichst konkret wahr, was du siehst oder hörst.",
                    "Notice as concretely as you can what you see or hear."
                ),
                tr(
                    "Wenn du wieder an dein Grübelthema denkst, bemerke das, ohne dich dafür zu kritisieren.",
                    "When you find yourself thinking about it again, notice that without criticizing yourself."
                ),
                tr(
                    "Kehre zu deiner gewählten Wahrnehmung zurück oder entscheide dich bewusst für eine andere Tätigkeit.",
                    "Return to what you chose to focus on, or deliberately choose a different activity."
                )
            ),
            tr(
                "Fertig, sobald du deine Aufmerksamkeit auf etwas anderes gerichtet hast.",
                "Done as soon as you have turned your attention to something else."
            ),
            tr("Die Gedanken dürfen trotzdem wiederkommen.", "The thoughts are still allowed to come back.")
        ),
        ex(
            "rumination_action", ExerciseCategory.Action, tr(
                "Den Gedanken in einen nächsten Schritt verwandeln",
                "Turning the thought into a next step"
            ),
            tr(
                "Aus wiederholtem Nachdenken eine konkrete, machbare Handlung entwickeln, wenn das Problem beeinflussbar ist.",
                "Turn repeated thinking into a concrete, doable action when the problem is something you can influence."
            ),
            listOf(
                tr(
                    "Frage dich: „Gibt es etwas, das ich wegen dieses Gedankens tatsächlich tun möchte?“",
                    "Ask yourself: “Is there something I actually want to do because of this thought?”"
                ),
                tr(
                    "Wenn ja, überlege, was der kleinste sinnvolle Schritt wäre.",
                    "If yes, think about what the smallest useful step would be."
                ),
                tr(
                    "Wähle eine konkrete Handlung, beispielsweise eine Information nachzuschauen, eine Frage aufzuschreiben oder ein Gespräch vorzubereiten.",
                    "Choose a concrete action, for example looking something up, writing down a question or preparing for a conversation."
                ),
                tr(
                    "Wenn du gerade nichts unternehmen kannst, entscheide bewusst, das Thema später erneut aufzugreifen, sofern das hilfreich ist.",
                    "If you can't do anything right now, deliberately decide to come back to it later, if that helps."
                ),
                tr(
                    "Wenn keine Handlung erforderlich ist, darfst du dich einer anderen Tätigkeit zuwenden.",
                    "If no action is needed, it's okay to turn to something else."
                )
            ),
            tr(
                "Fertig, sobald du einen nächsten Schritt gewählt oder erkannt hast, dass du gerade nichts tun musst.",
                "Done as soon as you have chosen a next step or realized that you don't have to do anything right now."
            ),
            tr("Du musst das gesamte Problem nicht lösen.", "You don't have to solve the whole problem.")
        ),
        ex(
            "rumination_support", ExerciseCategory.SelfSupport, tr(
                "Ich darf eine Frage offenlassen",
                "It's okay to leave a question open"
            ),
            tr(
                "Den Druck verringern, jede Unsicherheit sofort auflösen zu müssen.",
                "Ease the pressure to resolve every uncertainty right away."
            ),
            listOf(
                tr(
                    "Bemerke, ob du gerade versuchst, unbedingt eine sichere Antwort zu finden.",
                    "Notice whether you are trying hard to find a certain answer right now."
                ),
                tr(
                    "Sage dir innerlich: „Ich merke, dass ich darüber immer wieder nachdenke.“",
                    "Tell yourself: “I notice that I keep thinking about this.”"
                ),
                tr(
                    "Wähle einen Satz, der dir erlaubt, die Unsicherheit vorerst bestehen zu lassen, beispielsweise: „Ich muss das nicht in diesem Moment klären.“",
                    "Choose a sentence that lets the uncertainty stay for now, for example: “I don't have to settle this right now.”"
                ),
                tr(
                    "Erinnere dich daran, dass eine offene Frage nicht automatisch bedeutet, dass du etwas falsch machst.",
                    "Remind yourself that an open question doesn't automatically mean you are doing something wrong."
                ),
                tr(
                    "Entscheide, ob du dich jetzt einer anderen Tätigkeit zuwenden möchtest.",
                    "Decide whether you would like to turn to something else now."
                )
            ),
            tr(
                "Fertig, sobald du einen Satz gefunden hast, der die Frage offen sein lässt.",
                "Done as soon as you have found a sentence that lets the question stay open."
            ),
            tr("Du musst dich dadurch nicht sofort erleichtert fühlen.", "You don't have to feel relieved right away.")
        )
    )

    // ---------------------------------------------------------------- Traurigkeit
    private val sadness: List<Exercise> get() = listOf(
        ex(
            "sadness_body", ExerciseCategory.Body, tr("Fürsorglich mit dem Körper umgehen", "Caring for your body"),
            tr(
                "Eigene körperliche Bedürfnisse bei Traurigkeit wahrnehmen und sich etwas Unterstützung ermöglichen.",
                "Notice your physical needs when you are sad and allow yourself some support."
            ),
            listOf(
                tr("Nimm wahr, wie sich dein Körper gerade anfühlt.", "Notice how your body feels right now."),
                tr(
                    "Bemerke, ob du beispielsweise erschöpft bist, dich schwer fühlst oder Anspannung spürst.",
                    "Notice whether you are exhausted, feel heavy or feel tense, for example."
                ),
                tr(
                    "Frage dich: „Was wäre gerade angenehm oder entlastend für meinen Körper?“",
                    "Ask yourself: “What would feel pleasant or relieving for my body right now?”"
                ),
                tr(
                    "Wähle etwas Passendes, zum Beispiel eine bequemere Position, eine Decke, ein Glas Wasser oder eine sanfte Bewegung.",
                    "Choose something that fits, for example a more comfortable position, a blanket, a glass of water or a gentle movement."
                ),
                tr(
                    "Du darfst auch einfach in deiner jetzigen Position bleiben, wenn sich das am besten anfühlt.",
                    "It's also okay to simply stay in your current position if that feels best."
                )
            ),
            tr(
                "Fertig, sobald du eine Empfindung wahrgenommen und entschieden hast, was du gerade brauchst.",
                "Done as soon as you have noticed a sensation and decided what you need right now."
            ),
            tr("Du musst dich nicht besser fühlen.", "You don't have to feel better.")
        ),
        ex(
            "sadness_thoughts", ExerciseCategory.Thoughts, tr(
                "Freundlicher auf dich selbst schauen",
                "Looking at yourself more kindly"
            ),
            tr(
                "Belastende Gedanken über dich selbst erkennen und von Tatsachen unterscheiden.",
                "Recognize painful thoughts about yourself and tell them apart from facts."
            ),
            listOf(
                tr(
                    "Bemerke, welche Gedanken deine Traurigkeit begleiten.",
                    "Notice which thoughts come with your sadness."
                ),
                tr(
                    "Achte besonders auf Gedanken wie „Ich schaffe nichts“, „Alles ist sinnlos“ oder „Ich bin eine Belastung“.",
                    "Pay special attention to thoughts like “I can't do anything”, “Everything is pointless” or “I'm a burden”."
                ),
                tr(
                    "Frage dich: „Ist das eine Tatsache oder eine Bewertung, die ich gerade über mich habe?“",
                    "Ask yourself: “Is this a fact or a judgment I'm making about myself right now?”"
                ),
                tr(
                    "Überlege, ob es eine andere, ebenso mögliche Sichtweise gibt.",
                    "Think about whether there is another, equally possible way of seeing it."
                ),
                tr(
                    "Wenn dir keine alternative Sichtweise einfällt, erkenne zunächst nur, dass du diesen Gedanken gerade hast.",
                    "If no other view comes to mind, just notice for now that you are having this thought."
                )
            ),
            tr(
                "Fertig, sobald du einen belastenden Gedanken als Gedanken eingeordnet hast.",
                "Done as soon as you have recognized a painful thought as a thought."
            ),
            tr(
                "Du musst nicht davon überzeugt sein, dass alles gut wird.",
                "You don't have to be convinced that everything will be fine."
            )
        ),
        ex(
            "sadness_feelings", ExerciseCategory.Feelings, tr("Traurigkeit Raum geben", "Making room for sadness"),
            tr(
                "Traurigkeit wahrnehmen, ohne sie sofort verdrängen oder verändern zu müssen.",
                "Notice sadness without having to push it away or change it right away."
            ),
            listOf(
                tr("Sage dir innerlich: „Ich bin gerade traurig.“", "Tell yourself: “I'm sad right now.”"),
                tr("Nimm wahr, wie sich die Traurigkeit für dich zeigt.", "Notice how the sadness shows up for you."),
                tr(
                    "Vielleicht spürst du Schwere, Leere, Tränen oder etwas anderes. Du musst nichts Bestimmtes fühlen.",
                    "Maybe you feel heaviness, emptiness, tears or something else. You don't have to feel anything in particular."
                ),
                tr(
                    "Erlaube dir, traurig zu sein, ohne dich dafür zu verurteilen.",
                    "Allow yourself to be sad without judging yourself for it."
                ),
                tr(
                    "Erinnere dich daran: „Ich muss dieses Gefühl nicht sofort verändern.“",
                    "Remind yourself: “I don't have to change this feeling right away.”"
                )
            ),
            tr(
                "Fertig, sobald du deine Traurigkeit wahrgenommen und zugelassen hast.",
                "Done as soon as you have noticed your sadness and let it be."
            ),
            tr(
                "Die Traurigkeit muss weder verschwinden noch leichter werden.",
                "The sadness doesn't have to go away or get lighter."
            )
        ),
        ex(
            "sadness_attention", ExerciseCategory.Attention, tr(
                "Einen kleinen Lichtblick wahrnehmen",
                "Noticing a small bright spot"
            ),
            tr(
                "Neben der Traurigkeit auch andere Eindrücke wahrnehmen, ohne das Gefühl kleinzureden.",
                "Notice other impressions alongside the sadness, without downplaying the feeling."
            ),
            listOf(
                tr("Schau dich in deiner Umgebung um.", "Look around you."),
                tr(
                    "Suche nach einer Kleinigkeit, die deine Aufmerksamkeit auf sich zieht.",
                    "Look for a small thing that catches your attention."
                ),
                tr(
                    "Das kann eine Farbe, ein Gegenstand, ein Geräusch oder etwas Angenehmes sein.",
                    "It can be a color, an object, a sound or something pleasant."
                ),
                tr("Nimm diesen Eindruck bewusst wahr.", "Take in this impression mindfully."),
                tr(
                    "Wenn du möchtest, richte deine Aufmerksamkeit anschließend auf etwas, das dir normalerweise guttut.",
                    "If you like, then turn your attention to something that usually does you good."
                )
            ),
            tr(
                "Fertig, sobald du einen Eindruck aus deiner Umgebung wahrgenommen hast.",
                "Done as soon as you have noticed one impression from your surroundings."
            ),
            tr(
                "Du musst nichts Schönes finden. Auch ein neutraler Eindruck genügt.",
                "You don't have to find something beautiful. A neutral impression is enough."
            )
        ),
        ex(
            "sadness_action", ExerciseCategory.Action, tr(
                "Eine kleine Sache für dich tun",
                "Doing one small thing for yourself"
            ),
            tr(
                "Eine machbare Handlung finden, die zu deinen aktuellen Möglichkeiten passt.",
                "Find a doable action that fits what you can manage right now."
            ),
            listOf(
                tr(
                    "Frage dich: „Was würde mir gerade guttun oder meinen Alltag ein kleines Stück erleichtern?“",
                    "Ask yourself: “What would do me good right now or make my day a little easier?”"
                ),
                tr(
                    "Denke an einfache Möglichkeiten, zum Beispiel etwas trinken, dich hinlegen, etwas essen, ein Fenster öffnen oder eine vertraute Person kontaktieren.",
                    "Think of simple options, for example having a drink, lying down, eating something, opening a window or contacting someone you trust."
                ),
                tr("Wähle etwas, das sich für dich machbar anfühlt.", "Choose something that feels doable for you."),
                tr(
                    "Entscheide, ob du es jetzt tun möchtest oder ob Ruhe gerade wichtiger ist.",
                    "Decide whether you want to do it now or whether rest matters more right now."
                ),
                tr(
                    "Wenn selbst ein kleiner Schritt zu viel erscheint, darfst du die Handlung weglassen.",
                    "If even a small step seems like too much, it's okay to leave it."
                )
            ),
            tr(
                "Fertig, sobald du eine passende Handlung gewählt oder entschieden hast, dass du Ruhe brauchst.",
                "Done as soon as you have chosen a suitable action or decided that you need rest."
            ),
            tr(
                "Du musst nichts leisten, um Fürsorge zu verdienen.",
                "You don't have to achieve anything to deserve care."
            )
        ),
        ex(
            "sadness_support", ExerciseCategory.SelfSupport, tr(
                "So mit mir sprechen, wie ich es brauche",
                "Talking to myself the way I need"
            ),
            tr(
                "Dir selbst mit Verständnis begegnen, statt dich für deine Traurigkeit zusätzlich unter Druck zu setzen.",
                "Meet yourself with understanding instead of putting extra pressure on yourself for being sad."
            ),
            listOf(
                tr(
                    "Stelle dir vor, eine Person, die dir wichtig ist, würde sich gerade so traurig fühlen wie du.",
                    "Imagine someone who matters to you felt as sad as you do right now."
                ),
                tr("Überlege, was du dieser Person sagen würdest.", "Think about what you would say to them."),
                tr(
                    "Wähle daraus einen Satz, den du auch dir selbst zugestehen möchtest.",
                    "Pick one sentence from that which you would like to offer yourself too."
                ),
                tr(
                    "Beispiele: „Es ist in Ordnung, dass es mir gerade nicht gut geht.“ oder „Ich muss heute nicht alles schaffen.“",
                    "Examples: “It's okay that I'm not doing well right now.” or “I don't have to get everything done today.”"
                ),
                tr(
                    "Sage dir den Satz innerlich oder lies ihn dir bewusst durch.",
                    "Say the sentence to yourself or read it through mindfully."
                )
            ),
            tr(
                "Fertig, sobald du einen unterstützenden Satz gefunden oder ausgewählt hast.",
                "Done as soon as you have found or chosen a supportive sentence."
            ),
            tr(
                "Du musst ihn nicht sofort glauben oder dabei etwas Bestimmtes empfinden.",
                "You don't have to believe it right away or feel anything in particular."
            )
        )
    )

    // ---------------------------------------------------------------- Überforderung
    private val overwhelm: List<Exercise> get() = listOf(
        ex(
            "overwhelm_body", ExerciseCategory.Body, tr(
                "Den Druck im Körper wahrnehmen",
                "Noticing pressure in your body"
            ),
            tr(
                "Körperliche Anzeichen von Überforderung erkennen und eine angenehmere Haltung finden.",
                "Recognize physical signs of overwhelm and find a more comfortable posture."
            ),
            listOf(
                tr("Nimm wahr, wie sich dein Körper gerade anfühlt.", "Notice how your body feels right now."),
                tr(
                    "Achte darauf, ob du beispielsweise flach atmest, die Schultern hochziehst, die Zähne zusammenbeißt oder dich unruhig fühlst.",
                    "Notice whether you are breathing shallowly, pulling up your shoulders, clenching your teeth or feeling restless, for example."
                ),
                tr("Entscheide, ob du etwas verändern möchtest.", "Decide whether you would like to change anything."),
                tr(
                    "Lass deine Schultern sinken, lockere deine Hände oder verändere deine Sitzposition, wenn es angenehm ist.",
                    "Let your shoulders drop, loosen your hands or change how you are sitting if that feels good."
                ),
                tr(
                    "Du kannst auch einfach wahrnehmen, was gerade da ist, ohne etwas verändern zu müssen.",
                    "You can also simply notice what is there right now without having to change anything."
                )
            ),
            tr(
                "Fertig, sobald du eine körperliche Empfindung bemerkt und entschieden hast, ob du darauf reagieren möchtest.",
                "Done as soon as you have noticed a physical sensation and decided whether you want to respond to it."
            ),
            tr("Du musst dich nicht entspannter fühlen.", "You don't have to feel more relaxed.")
        ),
        ex(
            "overwhelm_thoughts", ExerciseCategory.Thoughts, tr(
                "Alles aus dem Kopf heraus sortieren",
                "Sorting everything out of your head"
            ),
            tr(
                "Gedanken ordnen und zwischen dem unterscheiden, was gerade wichtig ist und was warten kann.",
                "Sort your thoughts and tell apart what matters now and what can wait."
            ),
            listOf(
                tr(
                    "Bemerke, welche Aufgaben, Sorgen oder Erwartungen gleichzeitig in deinem Kopf auftauchen.",
                    "Notice which tasks, worries or expectations are showing up in your head at the same time."
                ),
                tr(
                    "Wähle einen Gedanken aus, der dich gerade besonders beschäftigt.",
                    "Pick one thought that is on your mind most right now."
                ),
                tr(
                    "Frage dich: „Muss ich mich wirklich jetzt darum kümmern?“",
                    "Ask yourself: “Do I really have to deal with this now?”"
                ),
                tr(
                    "Ordne den Gedanken einer dieser Möglichkeiten zu: jetzt wichtig, später möglich oder momentan nicht beeinflussbar.",
                    "Put the thought into one of these groups: important now, possible later, or out of my control right now."
                ),
                tr(
                    "Erinnere dich daran, dass nicht alles gleichzeitig gelöst werden muss.",
                    "Remind yourself that not everything has to be solved at once."
                )
            ),
            tr("Fertig, sobald du einen Gedanken eingeordnet hast.", "Done as soon as you have sorted one thought."),
            tr("Du musst nicht alle offenen Dinge sortieren.", "You don't have to sort out all open things.")
        ),
        ex(
            "overwhelm_feelings", ExerciseCategory.Feelings, tr("Überforderung anerkennen", "Acknowledging overwhelm"),
            tr(
                "Wahrnehmen, wie belastend die aktuelle Situation ist, ohne dich dafür zu verurteilen.",
                "Notice how stressful the current situation is without judging yourself for it."
            ),
            listOf(
                tr(
                    "Sage dir innerlich: „Gerade ist mir alles zu viel.“",
                    "Tell yourself: “Everything is too much for me right now.”"
                ),
                tr(
                    "Bemerke, welche Gefühle damit verbunden sind. Vielleicht fühlst du dich gestresst, ängstlich, gereizt, traurig oder hilflos.",
                    "Notice which feelings come with it. Maybe you feel stressed, anxious, irritable, sad or helpless."
                ),
                tr(
                    "Versuche nicht, alle Gefühle gleichzeitig zu erklären.",
                    "Don't try to explain all your feelings at once."
                ),
                tr(
                    "Erkenne an, dass deine aktuelle Belastung real ist.",
                    "Acknowledge that the strain you are under is real."
                ),
                tr(
                    "Erinnere dich daran: „Ich darf einen Schritt zurücktreten, auch wenn noch Dinge offen sind.“",
                    "Remind yourself: “It's okay to step back, even if things are still open.”"
                )
            ),
            tr(
                "Fertig, sobald du deine Überforderung oder ein Gefühl dazu wahrgenommen hast.",
                "Done as soon as you have noticed your overwhelm or a feeling that comes with it."
            ),
            tr(
                "Die Situation muss sich dadurch nicht weniger belastend anfühlen.",
                "The situation doesn't have to feel less stressful because of this."
            )
        ),
        ex(
            "overwhelm_attention", ExerciseCategory.Attention, tr(
                "Nur eine Sache gleichzeitig",
                "Just one thing at a time"
            ),
            tr(
                "Die Aufmerksamkeit von der Gesamtheit aller Anforderungen auf einen einzelnen gegenwärtigen Eindruck lenken.",
                "Shift your attention from all the demands at once to a single impression in the present."
            ),
            listOf(
                tr(
                    "Schau dich um und wähle einen Gegenstand in deiner Nähe.",
                    "Look around and pick an object near you."
                ),
                tr(
                    "Nimm seine Form, Farbe oder Oberfläche bewusst wahr.",
                    "Mindfully notice its shape, color or surface."
                ),
                tr(
                    "Bemerke, wenn deine Gedanken wieder zu anderen Aufgaben springen.",
                    "Notice when your thoughts jump back to other tasks."
                ),
                tr("Kehre freundlich zu dem Gegenstand zurück.", "Gently return to the object."),
                tr(
                    "Entscheide anschließend, ob du bei dieser Wahrnehmung bleiben oder dich einer einzelnen Tätigkeit zuwenden möchtest.",
                    "Then decide whether you want to stay with this or turn to a single task."
                )
            ),
            tr(
                "Fertig, sobald du einen Gegenstand bewusst wahrgenommen hast.",
                "Done as soon as you have mindfully noticed an object."
            ),
            tr(
                "Du musst deine Gedanken nicht vollständig beruhigen.",
                "You don't have to calm your thoughts completely."
            )
        ),
        ex(
            "overwhelm_action", ExerciseCategory.Action, tr(
                "Den nächsten kleinen Schritt wählen",
                "Choosing the next small step"
            ),
            tr(
                "Aus einer überwältigenden Gesamtsituation eine überschaubare nächste Handlung machen.",
                "Turn an overwhelming situation into one manageable next action."
            ),
            listOf(
                tr(
                    "Frage dich: „Was ist gerade wirklich notwendig?“",
                    "Ask yourself: “What is really necessary right now?”"
                ),
                tr(
                    "Wähle eine einzelne Aufgabe aus, die wichtig ist oder dir Erleichterung verschaffen könnte.",
                    "Pick a single task that is important or could bring you relief."
                ),
                tr(
                    "Überlege, was der kleinstmögliche Anfang wäre.",
                    "Think about what the smallest possible start would be."
                ),
                tr(
                    "Wenn die Aufgabe zu groß erscheint, teile sie weiter auf. Statt „Wohnung aufräumen“ könnte der erste Schritt beispielsweise sein, nur eine Tasse wegzustellen.",
                    "If the task seems too big, break it down further. Instead of “tidy the apartment”, the first step could be just putting away one cup."
                ),
                tr(
                    "Entscheide, ob du diesen Schritt jetzt angehen möchtest oder zunächst eine Pause brauchst.",
                    "Decide whether you want to take this step now or need a break first."
                )
            ),
            tr(
                "Fertig, sobald du einen kleinen Schritt gewählt oder dich für eine Pause entschieden hast.",
                "Done as soon as you have chosen a small step or decided to take a break."
            ),
            tr("Du musst nicht die gesamte Aufgabe erledigen.", "You don't have to finish the whole task.")
        ),
        ex(
            "overwhelm_support", ExerciseCategory.SelfSupport, tr(
                "Ich darf meine Grenzen beachten",
                "It's okay to respect my limits"
            ),
            tr(
                "Den eigenen Ansprüchen mit Verständnis begegnen und sich erlauben, Belastung zu begrenzen.",
                "Meet your own expectations with understanding and allow yourself to limit the strain."
            ),
            listOf(
                tr(
                    "Frage dich: „Was erwarte ich gerade alles von mir?“",
                    "Ask yourself: “What am I expecting of myself right now?”"
                ),
                tr(
                    "Überlege, ob diese Erwartungen angesichts deiner aktuellen Kraft realistisch sind.",
                    "Think about whether these expectations are realistic given your current energy."
                ),
                tr(
                    "Stelle dir vor, eine vertraute Person wäre in derselben Situation. Was würdest du ihr zugestehen?",
                    "Imagine someone close to you were in the same situation. What would you allow them?"
                ),
                tr(
                    "Wähle einen Satz, der dir ebenfalls entgegenkommen darf, zum Beispiel: „Ich muss nicht alles gleichzeitig schaffen.“",
                    "Choose a sentence you can offer yourself as well, for example: “I don't have to manage everything at once.”"
                ),
                tr(
                    "Überlege, ob du etwas verschieben, vereinfachen oder um Unterstützung bitten möchtest.",
                    "Think about whether you would like to postpone something, simplify it or ask for support."
                )
            ),
            tr(
                "Fertig, sobald du einen unterstützenden Satz gewählt oder eine Erwartung erkannt hast, die du verringern möchtest.",
                "Done as soon as you have chosen a supportive sentence or recognized an expectation you would like to lower."
            ),
            tr(
                "Du musst nicht sofort alle Anforderungen verändern.",
                "You don't have to change all demands right away."
            )
        )
    )

    // ---------------------------------------------------------------- Schlaflosigkeit
    private val insomnia: List<Exercise> get() = listOf(
        ex(
            "insomnia_body", ExerciseCategory.Body, tr(
                "Den Körper nicht zum Schlafen zwingen",
                "Not forcing your body to sleep"
            ),
            tr(
                "Körperliche Anspannung wahrnehmen und dem Körper erlauben, sich auszuruhen, ohne Schlaf erzwingen zu müssen.",
                "Notice physical tension and let your body rest without having to force sleep."
            ),
            listOf(
                tr("Nimm wahr, wie du gerade liegst oder sitzt.", "Notice how you are lying or sitting right now."),
                tr(
                    "Bemerke, ob du beispielsweise deine Schultern anspannst, die Zähne zusammenbeißt oder unruhig liegst.",
                    "Notice whether you are tensing your shoulders, clenching your teeth or lying restlessly, for example."
                ),
                tr(
                    "Verändere deine Position, wenn sich das angenehmer anfühlt.",
                    "Change your position if that feels more comfortable."
                ),
                tr(
                    "Lass deine Muskeln dort los, wo es dir leichtfällt. Du musst nicht den gesamten Körper entspannen.",
                    "Let go of your muscles wherever it comes easily. You don't have to relax your whole body."
                ),
                tr(
                    "Lass deinen Atem so fließen, wie er von selbst kommt. Du musst ihn weder vertiefen noch kontrollieren.",
                    "Let your breath flow the way it comes naturally. You don't have to deepen or control it."
                )
            ),
            tr(
                "Fertig, sobald du eine Empfindung wahrgenommen und deine Position bei Bedarf angepasst hast.",
                "Done as soon as you have noticed a sensation and adjusted your position if needed."
            ),
            tr(
                "Du musst dich nicht vollständig entspannen und nicht einschlafen.",
                "You don't have to relax completely or fall asleep."
            )
        ),
        ex(
            "insomnia_thoughts", ExerciseCategory.Thoughts, tr(
                "Den Schlafdruck loslassen",
                "Letting go of the pressure to sleep"
            ),
            tr(
                "Belastende Gedanken über das Wachliegen erkennen und den Druck reduzieren, jetzt unbedingt schlafen zu müssen.",
                "Recognize stressful thoughts about lying awake and ease the pressure to fall asleep right now."
            ),
            listOf(
                tr(
                    "Bemerke, welche Gedanken dir durch den Kopf gehen.",
                    "Notice which thoughts are going through your head."
                ),
                tr(
                    "Vielleicht denkst du: „Morgen bin ich völlig fertig“, „Ich muss jetzt endlich schlafen“ oder „Warum funktioniert das bei mir nicht?“",
                    "Maybe you think: “I'll be completely wrecked tomorrow”, “I finally have to sleep now” or “Why doesn't this work for me?”"
                ),
                tr(
                    "Erkenne, dass solche Gedanken zusätzlichen Druck erzeugen können.",
                    "Recognize that thoughts like these can create extra pressure."
                ),
                tr(
                    "Sage dir innerlich: „Ich bin gerade wach. Ich muss das nicht in diesem Moment lösen.“",
                    "Tell yourself: “I'm awake right now. I don't have to solve this in this moment.”"
                ),
                tr(
                    "Wenn du an den nächsten Tag denkst, erinnere dich daran, dass du dich morgen mit den dann tatsächlichen Anforderungen beschäftigen kannst.",
                    "When you think about tomorrow, remind yourself that you can deal with whatever actually comes up then."
                )
            ),
            tr(
                "Fertig, sobald du einen belastenden Gedanken erkannt und einen sanfteren Umgang damit gewählt hast.",
                "Done as soon as you have recognized a stressful thought and chosen a gentler way to deal with it."
            ),
            tr(
                "Du musst deine Gedanken nicht vollständig beruhigen.",
                "You don't have to calm your thoughts completely."
            )
        ),
        ex(
            "insomnia_feelings", ExerciseCategory.Feelings, tr(
                "Die Unruhe der Nacht anerkennen",
                "Acknowledging restlessness at night"
            ),
            tr(
                "Gefühle wahrnehmen, die beim Wachliegen auftreten, ohne gegen sie ankämpfen zu müssen.",
                "Notice the feelings that come up while lying awake without having to fight them."
            ),
            listOf(
                tr(
                    "Frage dich vorsichtig: „Wie fühlt sich das Wachliegen gerade für mich an?“",
                    "Gently ask yourself: “How does lying awake feel for me right now?”"
                ),
                tr(
                    "Vielleicht bemerkst du Frustration, Angst, Einsamkeit, Traurigkeit oder Ungeduld.",
                    "Maybe you notice frustration, fear, loneliness, sadness or impatience."
                ),
                tr(
                    "Wenn du ein Gefühl erkennst, benenne es innerlich.",
                    "If you recognize a feeling, name it to yourself."
                ),
                tr(
                    "Wenn du nichts Eindeutiges wahrnimmst, musst du nichts herausfinden.",
                    "If you don't notice anything clear, you don't have to figure anything out."
                ),
                tr(
                    "Erinnere dich daran: „Es ist verständlich, dass mich das Wachliegen belastet. Ich muss dieses Gefühl nicht sofort verändern.“",
                    "Remind yourself: “It makes sense that lying awake is hard for me. I don't have to change this feeling right away.”"
                )
            ),
            tr(
                "Fertig, sobald du ein Gefühl wahrgenommen oder anerkannt hast, dass es unklar ist.",
                "Done as soon as you have noticed a feeling or acknowledged that it is unclear."
            ),
            tr("Das Gefühl muss nicht verschwinden.", "The feeling doesn't have to go away.")
        ),
        ex(
            "insomnia_attention", ExerciseCategory.Attention, tr(
                "Bei einer ruhigen Wahrnehmung bleiben",
                "Staying with something calm"
            ),
            tr(
                "Die Aufmerksamkeit von kreisenden Gedanken auf eine neutrale oder angenehme Wahrnehmung lenken.",
                "Shift your attention from circling thoughts to something neutral or pleasant."
            ),
            listOf(
                tr(
                    "Wähle etwas Ruhiges, worauf du deine Aufmerksamkeit richten möchtest.",
                    "Choose something calm to focus on."
                ),
                tr(
                    "Du kannst beispielsweise das Gewicht deiner Decke, ein leises Umgebungsgeräusch oder das natürliche Kommen und Gehen deines Atems wahrnehmen.",
                    "For example, you can notice the weight of your blanket, a quiet sound around you or the natural coming and going of your breath."
                ),
                tr(
                    "Konzentriere dich auf eine einzelne Eigenschaft dieser Wahrnehmung.",
                    "Focus on one single quality of it."
                ),
                tr(
                    "Wenn deine Gedanken abschweifen, bemerke das, ohne dich zu kritisieren.",
                    "When your thoughts wander, notice that without criticizing yourself."
                ),
                tr(
                    "Kehre zur gewählten Wahrnehmung zurück, wenn du möchtest. Wenn sie unangenehm wird, wähle etwas anderes.",
                    "Return to what you chose if you like. If it becomes unpleasant, choose something else."
                )
            ),
            tr(
                "Fertig, sobald du eine ruhige Wahrnehmung bemerkt und deine Aufmerksamkeit darauf gerichtet hast.",
                "Done as soon as you have noticed something calm and turned your attention to it."
            ),
            tr("Du musst nicht schläfrig werden.", "You don't have to get sleepy.")
        ),
        ex(
            "insomnia_action", ExerciseCategory.Action, tr(
                "Entscheiden, was jetzt hilfreich ist",
                "Deciding what helps now"
            ),
            tr(
                "Eine passende Entscheidung für die aktuelle Schlafsituation treffen, statt sich hilflos im Wachliegen festzufahren.",
                "Make a decision that fits your current sleep situation instead of feeling stuck lying awake."
            ),
            listOf(
                tr(
                    "Frage dich: „Was brauche ich gerade – Ruhe, eine Veränderung oder weniger Druck?“",
                    "Ask yourself: “What do I need right now – rest, a change or less pressure?”"
                ),
                tr(
                    "Überlege, ob es sich angenehm anfühlt, noch etwas liegen zu bleiben.",
                    "Think about whether it feels okay to stay in bed a little longer."
                ),
                tr(
                    "Wenn du zunehmend frustriert oder unruhig wirst, kannst du aufstehen und bei gedämpftem Licht etwas Ruhiges tun.",
                    "If you are getting more frustrated or restless, you can get up and do something calm in dim light."
                ),
                tr(
                    "Wähle eine wenig anregende Tätigkeit, beispielsweise etwas Ruhiges lesen oder leise Musik hören.",
                    "Choose an activity that isn't stimulating, for example reading something calm or listening to quiet music."
                ),
                tr(
                    "Kehre ins Bett zurück, wenn du wieder schläfriger wirst. Du musst dabei nicht auf die Uhr schauen.",
                    "Go back to bed when you feel sleepier. You don't have to look at the clock."
                )
            ),
            tr(
                "Fertig, sobald du entschieden hast, ob du liegen bleibst oder kurz aufstehst.",
                "Done as soon as you have decided whether to stay in bed or get up for a while."
            ),
            tr(
                "Du musst nicht sofort einschlafen. Wenn Schlaflosigkeit häufig auftritt oder dich stark belastet, kann es sinnvoll sein, sie ärztlich abklären zu lassen.",
                "You don't have to fall asleep right away. If insomnia happens often or weighs on you a lot, it can make sense to have it checked by a doctor."
            )
        ),
        ex(
            "insomnia_support", ExerciseCategory.SelfSupport, tr(
                "Auch eine schwierige Nacht bewältigen",
                "Getting through a difficult night"
            ),
            tr(
                "Mit sich selbst verständnisvoll umgehen und die Angst vor den Folgen einer schlechten Nacht etwas entlasten.",
                "Be understanding with yourself and ease some of the worry about the effects of a bad night."
            ),
            listOf(
                tr(
                    "Bemerke, ob du dich wegen des Wachliegens unter Druck setzt oder dir Vorwürfe machst.",
                    "Notice whether you are putting pressure on yourself or blaming yourself for lying awake."
                ),
                tr(
                    "Stelle dir vor, eine Person, die dir wichtig ist, würde gerade dieselbe Nacht erleben.",
                    "Imagine someone who matters to you were having the same night right now."
                ),
                tr(
                    "Überlege, was du ihr sagen würdest, ohne ihre Schwierigkeiten kleinzureden.",
                    "Think about what you would say to them without downplaying their difficulties."
                ),
                tr(
                    "Wähle einen Satz, den du dir selbst zugestehen möchtest, zum Beispiel: „Ich darf mich ausruhen, auch wenn ich gerade nicht schlafe.“",
                    "Choose a sentence you would like to offer yourself, for example: “It's okay to rest, even if I'm not sleeping right now.”"
                ),
                tr(
                    "Erinnere dich daran, dass eine schwierige Nacht nicht bedeutet, dass du versagt hast.",
                    "Remind yourself that a difficult night doesn't mean you have failed."
                )
            ),
            tr(
                "Fertig, sobald du einen unterstützenden Satz gewählt oder dir erlaubt hast, ohne Vorwürfe wach zu sein.",
                "Done as soon as you have chosen a supportive sentence or allowed yourself to be awake without blame."
            ),
            tr(
                "Du musst dich dadurch nicht sofort sicherer oder müder fühlen.",
                "You don't have to feel safer or sleepier right away."
            )
        )
    )

    // ---------------------------------------------------------------- Einsamkeit
    private val loneliness: List<Exercise> get() = listOf(
        ex(
            "loneliness_body", ExerciseCategory.Body, tr("Sich selbst Geborgenheit geben", "Giving yourself comfort"),
            tr(
                "Körperliche Bedürfnisse wahrnehmen und ein Gefühl von Komfort oder Sicherheit unterstützen.",
                "Notice your physical needs and support a sense of comfort or safety."
            ),
            listOf(
                tr("Nimm wahr, wie sich dein Körper gerade anfühlt.", "Notice how your body feels right now."),
                tr(
                    "Bemerke, ob du dich angespannt, unruhig, erschöpft oder vielleicht leer fühlst.",
                    "Notice whether you feel tense, restless, exhausted or maybe empty."
                ),
                tr(
                    "Überlege, was sich gerade angenehm anfühlen könnte.",
                    "Think about what could feel pleasant right now."
                ),
                tr(
                    "Wähle etwas Passendes: Wickle dich in eine Decke, halte eine warme Tasse, setze dich bequem hin oder lege eine Hand auf deinen Arm, wenn sich das gut anfühlt.",
                    "Choose something that fits: wrap yourself in a blanket, hold a warm cup, sit comfortably or place a hand on your arm if that feels good."
                ),
                tr(
                    "Lass dir die Freiheit, eine andere Möglichkeit zu wählen oder nichts zu verändern.",
                    "Give yourself the freedom to choose something else or to change nothing."
                )
            ),
            tr(
                "Fertig, sobald du eine Empfindung wahrgenommen und eine Form von Komfort gewählt hast – oder nichts davon möchtest.",
                "Done as soon as you have noticed a sensation and chosen a kind of comfort – or decided you don't want any."
            ),
            tr(
                "Du musst dich nicht geborgen oder weniger einsam fühlen.",
                "You don't have to feel safe or less lonely."
            )
        ),
        ex(
            "loneliness_thoughts", ExerciseCategory.Thoughts, tr(
                "Einsamkeit ist nicht mein Wert",
                "Loneliness is not my worth"
            ),
            tr(
                "Gedanken über den eigenen Wert und die Zugehörigkeit hinterfragen, ohne die Einsamkeit kleinzureden.",
                "Question thoughts about your worth and belonging without downplaying the loneliness."
            ),
            listOf(
                tr(
                    "Bemerke, welche Gedanken auftauchen, wenn du dich einsam fühlst.",
                    "Notice which thoughts come up when you feel lonely."
                ),
                tr(
                    "Vielleicht denkst du: „Niemand interessiert sich für mich“ oder „Ich gehöre nirgendwo dazu“.",
                    "Maybe you think: “Nobody cares about me” or “I don't belong anywhere”."
                ),
                tr(
                    "Frage dich: „Beschreibt dieser Gedanke meine aktuelle Situation oder bewertet er mich als Person?“",
                    "Ask yourself: “Does this thought describe my current situation, or does it judge me as a person?”"
                ),
                tr(
                    "Überlege, ob du zwischen dem Gefühl, gerade allein zu sein, und deinem Wert als Mensch unterscheiden kannst.",
                    "Think about whether you can tell apart the feeling of being alone right now and your worth as a person."
                ),
                tr(
                    "Wenn dir keine andere Sichtweise einfällt, erkenne zunächst nur, dass Einsamkeit solche Gedanken hervorrufen kann.",
                    "If no other view comes to mind, just notice for now that loneliness can bring up thoughts like these."
                )
            ),
            tr(
                "Fertig, sobald du einen Gedanken über dich geprüft hast: Tatsache oder Bewertung?",
                "Done as soon as you have checked one thought about yourself: fact or judgment?"
            ),
            tr(
                "Du musst dich nicht davon überzeugen, dass du nicht einsam bist.",
                "You don't have to convince yourself that you are not lonely."
            )
        ),
        ex(
            "loneliness_feelings", ExerciseCategory.Feelings, tr(
                "Das Bedürfnis hinter der Einsamkeit erkennen",
                "Recognizing the need behind loneliness"
            ),
            tr(
                "Wahrnehmen, welche Gefühle und Bedürfnisse mit der Einsamkeit verbunden sind.",
                "Notice which feelings and needs are connected to the loneliness."
            ),
            listOf(
                tr("Sage dir innerlich: „Ich fühle mich gerade einsam.“", "Tell yourself: “I feel lonely right now.”"),
                tr(
                    "Bemerke, welche Gefühle damit verbunden sind. Vielleicht sind es Traurigkeit, Sehnsucht, Leere, Enttäuschung oder der Wunsch, verstanden zu werden.",
                    "Notice which feelings come with it. Maybe it's sadness, longing, emptiness, disappointment or the wish to be understood."
                ),
                tr(
                    "Frage dich: „Was fehlt mir gerade am meisten?“",
                    "Ask yourself: “What am I missing most right now?”"
                ),
                tr(
                    "Vielleicht wünschst du dir Nähe, ein Gespräch, Verständnis oder einfach die Anwesenheit eines anderen Menschen.",
                    "Maybe you wish for closeness, a conversation, understanding or simply someone else being there."
                ),
                tr(
                    "Erkenne dieses Bedürfnis an, ohne dich dafür zu verurteilen.",
                    "Acknowledge this need without judging yourself for it."
                )
            ),
            tr(
                "Fertig, sobald du ein Gefühl oder Bedürfnis hinter der Einsamkeit benannt hast.",
                "Done as soon as you have named a feeling or need behind the loneliness."
            ),
            tr(
                "Du musst dieses Bedürfnis nicht sofort erfüllen können.",
                "You don't have to be able to meet this need right away."
            )
        ),
        ex(
            "loneliness_attention", ExerciseCategory.Attention, tr(
                "Verbindung zur Umgebung wahrnehmen",
                "Noticing connection to your surroundings"
            ),
            tr(
                "Die eigene Aufmerksamkeit bewusst auf die gegenwärtige Umgebung richten, ohne die Einsamkeit verdrängen zu müssen.",
                "Deliberately turn your attention to your surroundings without having to push the loneliness away."
            ),
            listOf(
                tr(
                    "Schau dich an dem Ort um, an dem du gerade bist.",
                    "Look around the place where you are right now."
                ),
                tr(
                    "Suche einen Gegenstand, eine Pflanze, ein Fenster oder etwas anderes, das deine Aufmerksamkeit auf sich zieht.",
                    "Find an object, a plant, a window or something else that catches your attention."
                ),
                tr(
                    "Nimm eine konkrete Eigenschaft wahr, zum Beispiel eine Farbe, eine Form oder ein Geräusch.",
                    "Notice one specific quality, for example a color, a shape or a sound."
                ),
                tr(
                    "Bemerke auch, ob du etwas wahrnimmst, das dir vertraut oder angenehm ist.",
                    "Also notice whether you perceive something familiar or pleasant."
                ),
                tr(
                    "Wenn deine Gedanken wieder zur Einsamkeit zurückkehren, ist das in Ordnung. Du kannst deine Aufmerksamkeit erneut auf deine Umgebung richten.",
                    "If your thoughts return to the loneliness, that's okay. You can turn your attention back to your surroundings."
                )
            ),
            tr(
                "Fertig, sobald du einen Eindruck aus deiner Umgebung wahrgenommen hast.",
                "Done as soon as you have noticed one impression from your surroundings."
            ),
            tr(
                "Du musst dich dadurch nicht verbundener fühlen.",
                "You don't have to feel more connected because of this."
            )
        ),
        ex(
            "loneliness_action", ExerciseCategory.Action, tr(
                "Eine kleine Verbindung ermöglichen",
                "Making a small connection"
            ),
            tr(
                "Eine Form von Kontakt oder Nähe finden, die den eigenen Bedürfnissen und Möglichkeiten entspricht.",
                "Find a kind of contact or closeness that fits your needs and what you can manage."
            ),
            listOf(
                tr(
                    "Frage dich: „Würde mir gerade Kontakt zu einem anderen Menschen guttun?“",
                    "Ask yourself: “Would contact with another person do me good right now?”"
                ),
                tr(
                    "Überlege, welche Form von Kontakt passend wäre. Vielleicht möchtest du jemandem schreiben, telefonieren, eine vertraute Person treffen oder dich unter Menschen aufhalten.",
                    "Think about what kind of contact would fit. Maybe you would like to text someone, call, meet someone you trust or be around people."
                ),
                tr("Wähle eine Möglichkeit, die sich machbar anfühlt.", "Choose an option that feels doable."),
                tr(
                    "Falls du Kontakt aufnehmen möchtest, kannst du eine einfache Nachricht schreiben, beispielsweise: „Hey, hast du gerade etwas Zeit zum Reden?“",
                    "If you want to reach out, you can write a simple message, for example: “Hey, do you have a moment to talk?”"
                ),
                tr(
                    "Wenn du gerade keinen Kontakt möchtest oder niemand erreichbar ist, überlege, was dir stattdessen guttun könnte.",
                    "If you don't want contact right now or no one is available, think about what else could do you good."
                )
            ),
            tr(
                "Fertig, sobald du eine Form von Kontakt gewählt oder entschieden hast, was du stattdessen brauchst.",
                "Done as soon as you have chosen a kind of contact or decided what you need instead."
            ),
            tr(
                "Du musst keine Antwort erhalten und keinen Kontakt erzwingen.",
                "You don't have to get a reply or force contact."
            )
        ),
        ex(
            "loneliness_support", ExerciseCategory.SelfSupport, tr(
                "Ich darf mir Nähe wünschen",
                "It's okay to wish for closeness"
            ),
            tr(
                "Das eigene Bedürfnis nach Nähe ernst nehmen und sich nicht für Einsamkeit oder Sehnsucht verurteilen.",
                "Take your need for closeness seriously and don't judge yourself for loneliness or longing."
            ),
            listOf(
                tr(
                    "Überlege, was du dir gerade von anderen Menschen wünschst.",
                    "Think about what you wish for from other people right now."
                ),
                tr(
                    "Erinnere dich daran, dass der Wunsch nach Nähe, Verständnis und Zugehörigkeit menschlich ist.",
                    "Remind yourself that wishing for closeness, understanding and belonging is human."
                ),
                tr(
                    "Bemerke, ob du dir selbst Vorwürfe machst, weil du einsam bist.",
                    "Notice whether you are blaming yourself for being lonely."
                ),
                tr(
                    "Wähle einen Satz, der dir mit Verständnis begegnet, zum Beispiel: „Es ist verständlich, dass ich mir Nähe wünsche.“",
                    "Choose a sentence that meets you with understanding, for example: “It makes sense that I wish for closeness.”"
                ),
                tr(
                    "Wenn es dir hilft, überlege, welche Person oder welcher Ort dir grundsätzlich ein Gefühl von Verbundenheit geben kann.",
                    "If it helps, think about which person or place can generally give you a sense of connection."
                )
            ),
            tr(
                "Fertig, sobald du einen unterstützenden Satz gewählt oder eine Person oder einen Ort der Verbundenheit benannt hast.",
                "Done as soon as you have chosen a supportive sentence or named a person or place of connection."
            ),
            tr("Du musst dich nicht sofort weniger einsam fühlen.", "You don't have to feel less lonely right away.")
        )
    )

    // ---------------------------------------------------------------- Ich weiß nicht genau
    private val unknown: List<Exercise> get() = listOf(
        ex(
            "unknown_body", ExerciseCategory.Body, tr(
                "Wahrnehmen, was gerade da ist",
                "Noticing what is here right now"
            ),
            tr(
                "Einen ersten Zugang zum eigenen Zustand finden, ohne das Gefühl bereits benennen zu müssen.",
                "Find a first way into how you are doing without having to name the feeling yet."
            ),
            listOf(
                tr("Nimm wahr, wie dein Körper sich gerade anfühlt.", "Notice how your body feels right now."),
                tr(
                    "Bemerke, ob du beispielsweise Anspannung, Müdigkeit, Unruhe, Schwere oder gar nichts Bestimmtes wahrnimmst.",
                    "Notice whether you feel tension, tiredness, restlessness, heaviness or nothing in particular, for example."
                ),
                tr(
                    "Richte deine Aufmerksamkeit auf eine Körperstelle, die du gerade gut spüren kannst.",
                    "Turn your attention to a part of your body you can feel well right now."
                ),
                tr(
                    "Beschreibe die Empfindung innerlich möglichst einfach, etwa als warm, kalt, schwer, ruhig oder angespannt.",
                    "Describe the sensation to yourself as simply as you can, for example as warm, cold, heavy, calm or tense."
                ),
                tr(
                    "Wenn du keine eindeutige Empfindung findest, kannst du auch das einfach feststellen.",
                    "If you can't find a clear sensation, you can simply notice that too."
                )
            ),
            tr(
                "Fertig, sobald du eine Empfindung wahrgenommen oder gemerkt hast, dass du gerade nichts Eindeutiges spürst.",
                "Done as soon as you have noticed a sensation or realized that you don't feel anything clear right now."
            ),
            tr(
                "Du musst nicht herausfinden, was diese Empfindung bedeutet.",
                "You don't have to figure out what this sensation means."
            )
        ),
        ex(
            "unknown_thoughts", ExerciseCategory.Thoughts, tr(
                "Den inneren Zustand beschreiben",
                "Describing your inner state"
            ),
            tr(
                "Erste Hinweise auf den eigenen Zustand erkennen, ohne sich zu einer eindeutigen Erklärung zu zwingen.",
                "Recognize first clues about how you are doing without forcing a clear explanation."
            ),
            listOf(
                tr(
                    "Frage dich: „Was geht mir gerade durch den Kopf?“",
                    "Ask yourself: “What is going through my head right now?”"
                ),
                tr(
                    "Bemerke, ob deine Gedanken eher ruhig, schnell, unübersichtlich, leer oder kreisend sind.",
                    "Notice whether your thoughts are rather calm, fast, jumbled, empty or circling."
                ),
                tr(
                    "Versuche, einen Gedanken zu benennen, der gerade besonders präsent ist.",
                    "Try to name one thought that is especially present right now."
                ),
                tr(
                    "Frage dich, ob dieser Gedanke möglicherweise mit deinem aktuellen Zustand zusammenhängt.",
                    "Ask yourself whether this thought might be connected to how you are doing."
                ),
                tr(
                    "Wenn du keinen Zusammenhang erkennst, lasse die Frage offen.",
                    "If you don't see a connection, leave the question open."
                )
            ),
            tr(
                "Fertig, sobald du einen Gedanken oder die Art deiner Gedanken wahrgenommen hast.",
                "Done as soon as you have noticed a thought or the kind of thoughts you are having."
            ),
            tr(
                "Du musst keine Erklärung für deinen Zustand finden.",
                "You don't have to find an explanation for how you are doing."
            )
        ),
        ex(
            "unknown_feelings", ExerciseCategory.Feelings, tr(
                "Ich muss es nicht sofort wissen",
                "I don't have to know right away"
            ),
            tr(
                "Unsicherheit über die eigenen Gefühle zulassen, ohne zusätzlichen Druck aufzubauen.",
                "Allow uncertainty about your feelings without building up extra pressure."
            ),
            listOf(
                tr(
                    "Sage dir innerlich: „Ich weiß gerade nicht genau, was ich fühle.“",
                    "Tell yourself: “I don't really know what I'm feeling right now.”"
                ),
                tr(
                    "Bemerke, ob dieser Umstand selbst etwas in dir auslöst, beispielsweise Unsicherheit, Frustration oder Gleichgültigkeit.",
                    "Notice whether this itself brings something up in you, for example uncertainty, frustration or indifference."
                ),
                tr(
                    "Versuche nicht, sofort ein eindeutiges Gefühl auszuwählen.",
                    "Don't try to pick one clear feeling right away."
                ),
                tr(
                    "Erlaube dir, mehrere Empfindungen gleichzeitig zu haben oder gerade keinen klaren Zugang zu deinen Gefühlen zu finden.",
                    "Allow yourself to have several sensations at once, or to not have clear access to your feelings right now."
                ),
                tr(
                    "Erinnere dich daran, dass du deinen Zustand nicht vollständig verstehen musst, um für dich sorgen zu dürfen.",
                    "Remind yourself that you don't have to fully understand how you are doing in order to take care of yourself."
                )
            ),
            tr(
                "Fertig, sobald du anerkannt hast, dass dein Gefühl gerade unklar ist.",
                "Done as soon as you have acknowledged that your feeling is unclear right now."
            ),
            tr("Du musst kein bestimmtes Gefühl benennen.", "You don't have to name a particular feeling.")
        ),
        ex(
            "unknown_attention", ExerciseCategory.Attention, tr(
                "Die Umgebung als Orientierung nutzen",
                "Using your surroundings to get your bearings"
            ),
            tr(
                "Einen einfachen Bezug zum gegenwärtigen Moment finden, wenn der eigene innere Zustand unklar ist.",
                "Find a simple connection to the present moment when your inner state is unclear."
            ),
            listOf(
                tr("Schau dich in deiner Umgebung um.", "Look around you."),
                tr("Wähle einen Gegenstand, den du gut sehen kannst.", "Choose an object you can see well."),
                tr(
                    "Nimm seine Farbe, Form oder Position bewusst wahr.",
                    "Mindfully notice its color, shape or position."
                ),
                tr(
                    "Höre anschließend auf ein Geräusch oder spüre, wie deine Füße den Boden berühren.",
                    "Then listen to a sound or feel your feet touching the floor."
                ),
                tr(
                    "Bemerke, dass du deine Aufmerksamkeit zwischen deiner Umgebung und deinem inneren Erleben bewegen kannst.",
                    "Notice that you can move your attention between your surroundings and what you experience inside."
                )
            ),
            tr(
                "Fertig, sobald du einen Gegenstand und eine weitere Sinneswahrnehmung bemerkt hast.",
                "Done as soon as you have noticed an object and one more sensory impression."
            ),
            tr(
                "Du musst dich danach weder ruhiger noch klarer fühlen.",
                "You don't have to feel calmer or clearer afterwards."
            )
        ),
        ex(
            "unknown_action", ExerciseCategory.Action, tr(
                "Herausfinden, was ich gerade brauche",
                "Finding out what I need right now"
            ),
            tr(
                "Eine passende nächste Handlung wählen, auch wenn das eigene Gefühl noch unklar ist.",
                "Choose a suitable next action, even if your feeling is still unclear."
            ),
            listOf(
                tr(
                    "Frage dich: „Was könnte mir gerade helfen, unabhängig davon, was ich genau fühle?“",
                    "Ask yourself: “What could help me right now, regardless of what exactly I'm feeling?”"
                ),
                tr(
                    "Überlege, ob du ein grundlegendes Bedürfnis wahrnimmst: Ruhe, Bewegung, etwas zu trinken, Nahrung, Kontakt oder Abstand.",
                    "Think about whether you notice a basic need: rest, movement, something to drink, food, contact or distance."
                ),
                tr(
                    "Wenn du unsicher bist, wähle eine einfache Möglichkeit, die sich angenehm oder sinnvoll anfühlt.",
                    "If you are unsure, choose a simple option that feels pleasant or sensible."
                ),
                tr(
                    "Du könntest beispielsweise ein Glas Wasser trinken, dich bequemer hinsetzen, das Fenster öffnen oder eine vertraute Person kontaktieren.",
                    "For example, you could drink a glass of water, sit more comfortably, open the window or contact someone you trust."
                ),
                tr(
                    "Wenn du gerade nichts tun möchtest, darfst du auch zunächst bleiben, wo du bist.",
                    "If you don't want to do anything right now, it's okay to stay where you are for now."
                )
            ),
            tr(
                "Fertig, sobald du eine hilfreiche Handlung gewählt oder entschieden hast, vorerst nichts zu verändern.",
                "Done as soon as you have chosen a helpful action or decided not to change anything for now."
            ),
            tr("Du musst nicht wissen, warum du etwas brauchst.", "You don't have to know why you need something.")
        ),
        ex(
            "unknown_support", ExerciseCategory.SelfSupport, tr(
                "Mir selbst Orientierung geben",
                "Giving myself orientation"
            ),
            tr(
                "Freundlich mit sich selbst umgehen, wenn die eigenen Gefühle schwer einzuordnen sind.",
                "Be kind to yourself when your feelings are hard to make sense of."
            ),
            listOf(
                tr(
                    "Bemerke, ob du von dir erwartest, genau erklären zu können, wie es dir geht.",
                    "Notice whether you expect yourself to be able to explain exactly how you are doing."
                ),
                tr(
                    "Erinnere dich daran, dass Gefühle und innere Zustände nicht immer eindeutig sind.",
                    "Remind yourself that feelings and inner states are not always clear-cut."
                ),
                tr(
                    "Überlege, welcher Satz dir in dieser Situation Halt geben könnte.",
                    "Think about which sentence could give you support in this situation."
                ),
                tr(
                    "Wähle beispielsweise: „Ich darf mir Zeit lassen, um herauszufinden, wie es mir geht.“",
                    "For example, choose: “I can take my time to find out how I'm doing.”"
                ),
                tr(
                    "Wenn es dir hilft, überlege, was du einer vertrauten Person sagen würdest, die ihren eigenen Zustand gerade nicht versteht.",
                    "If it helps, think about what you would say to someone close to you who doesn't understand how they are doing right now."
                )
            ),
            tr(
                "Fertig, sobald du einen unterstützenden Satz gewählt oder dir erlaubt hast, deinen Zustand nicht verstehen zu müssen.",
                "Done as soon as you have chosen a supportive sentence or allowed yourself not to have to understand how you are doing."
            ),
            tr("Du musst keine Klarheit erreichen.", "You don't have to reach clarity.")
        )
    )

    val all: List<Situation> get() = listOf(
        Situation("anxiety", tr("Angst", "Anxiety"), Icons.Outlined.Shield, shown(anxiety)),
        Situation("panic", tr("Panik", "Panic"), Icons.Outlined.Waves, shown(panic)),
        Situation("anger", tr("Wut", "Anger"), Icons.Outlined.LocalFireDepartment, shown(anger)),
        Situation("rumination", tr("Grübeln", "Rumination"), Icons.Outlined.Psychology, shown(rumination)),
        Situation("sadness", tr("Traurigkeit", "Sadness"), Icons.Outlined.SentimentDissatisfied, shown(sadness)),
        Situation("overwhelm", tr("Überforderung", "Overwhelm"), Icons.Outlined.Layers, shown(overwhelm)),
        Situation("insomnia", tr("Schlaflosigkeit", "Insomnia"), Icons.Outlined.NightsStay, shown(insomnia)),
        Situation("loneliness", tr("Einsamkeit", "Loneliness"), Icons.Outlined.PersonOutline, shown(loneliness)),
        Situation("unknown", tr("Allgemein", "General"), Icons.Outlined.Explore, shown(unknown))
    )

    fun fromId(id: String?): Situation? = all.firstOrNull { it.id == id }

    fun findExercise(id: String?): Exercise? =
        all.flatMap { it.exercises }.firstOrNull { it.id == id }
}
