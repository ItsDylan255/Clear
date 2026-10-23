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

/** Die sechs Uebungskategorien. Sie bleiben bei jeder Emotion gleich. */
enum class ExerciseCategory(val label: String) {
    Body("Körper"),
    Thoughts("Gedanken"),
    Feelings("Gefühle"),
    Attention("Aufmerksamkeit"),
    Action("Handlung"),
    SelfSupport("Selbstunterstützung")
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
    private val anxiety = listOf(
        ex(
            "anxiety_body", ExerciseCategory.Body, "Anspannung loslassen",
            "Körperliche Anspannung wahrnehmen und etwas lockern.",
            listOf(
                "Setze dich bequem hin oder stelle dich stabil hin.",
                "Bemerke, wo du Anspannung spürst, beispielsweise in Schultern, Händen oder Kiefer.",
                "Lass die Schultern sanft sinken und lockere deine Hände.",
                "Spüre nach, ohne etwas erzwingen zu müssen."
            ),
            "Fertig, sobald du die Anspannung wahrgenommen und Schultern und Hände gelockert hast.",
            "Du musst dich nicht vollständig entspannen. Schon das Wahrnehmen reicht."
        ),
        ex(
            "anxiety_thoughts", ExerciseCategory.Thoughts, "Befürchtung oder Tatsache?",
            "Angstgedanken betrachten, ohne sie automatisch für wahr zu halten.",
            listOf(
                "Bemerke, wovor du gerade Angst hast.",
                "Formuliere den Gedanken: „Ich befürchte, dass …“",
                "Frage dich: „Was spricht dafür, was dagegen? Was weiß ich tatsächlich?“",
                "Erinnere dich daran, dass du nicht jede Unsicherheit sofort auflösen musst."
            ),
            "Fertig, sobald du zwischen Wissen und Befürchtung unterschieden hast.",
            "Du musst keine eindeutige Antwort finden."
        ),
        ex(
            "anxiety_feelings", ExerciseCategory.Feelings, "Der Angst Raum geben",
            "Angst anerkennen, ohne zusätzlich gegen sie anzukämpfen.",
            listOf(
                "Sage dir innerlich: „Ich spüre gerade Angst.“",
                "Nimm wahr, wie sie sich anfühlt.",
                "Versuche, das Gefühl für einen Moment da sein zu lassen.",
                "Erinnere dich: „Ich darf Angst haben und trotzdem Schritt für Schritt weitermachen.“"
            ),
            "Fertig, sobald du deine Angst wahrgenommen und für einen Moment zugelassen hast.",
            "Du musst die Angst weder mögen noch vollständig akzeptieren können."
        ),
        ex(
            "anxiety_attention", ExerciseCategory.Attention, "Zurück ins Hier und Jetzt",
            "Die Aufmerksamkeit von angstauslösenden Gedanken auf die gegenwärtige Umgebung lenken.",
            listOf(
                "Schau dich langsam um.",
                "Suche fünf Dinge, die du sehen kannst.",
                "Nimm drei Geräusche wahr.",
                "Spüre, wo dein Körper den Boden oder die Sitzfläche berührt.",
                "Richte deine Aufmerksamkeit wieder auf das, was du gerade wahrnimmst."
            ),
            "Fertig, sobald du fünf Dinge, drei Geräusche und den Kontakt zum Boden wahrgenommen hast."
        ),
        ex(
            "anxiety_action", ExerciseCategory.Action, "Ein kleiner Schritt",
            "Trotz Angst wieder etwas Handlungsspielraum gewinnen.",
            listOf(
                "Überlege, welche Situation dich gerade beschäftigt.",
                "Frage dich: „Was wäre ein kleiner, sicherer Schritt, der mir jetzt helfen könnte?“",
                "Wähle eine überschaubare Handlung, zum Beispiel eine Nachricht schreiben, ein Glas Wasser holen oder eine Aufgabe in einem kleinen Teil beginnen.",
                "Konzentriere dich nur auf diesen Schritt."
            ),
            "Fertig, sobald du einen kleinen, sicheren Schritt gewählt oder dich für eine Pause entschieden hast.",
            "Du musst nicht die gesamte Situation lösen."
        ),
        ex(
            "anxiety_support", ExerciseCategory.SelfSupport, "Mein eigener Halt",
            "In einem schwierigen Moment einen hilfreichen inneren Anker finden.",
            listOf(
                "Stell dir vor, ein guter Freund hätte gerade genau diese Angst.",
                "Überlege, was du ihm verständnisvoll sagen würdest.",
                "Wähle einen Satz, der sich für dich passend anfühlt.",
                "Wiederhole ihn langsam, beispielsweise: „Ich muss das gerade nicht alles auf einmal lösen.“"
            ),
            "Fertig, sobald du einen hilfreichen Satz für dich gefunden hast."
        )
    )

    // ---------------------------------------------------------------- Panik
    private val panic = listOf(
        ex(
            "panic_body", ExerciseCategory.Body, "Eine bequeme Position finden",
            "Dem Körper Halt geben, ohne ihn zu einer bestimmten Reaktion zu zwingen.",
            listOf(
                "Setze dich hin, wenn das für dich sicher und angenehm ist. Alternativ kannst du stehen bleiben und dich an etwas Stabilem orientieren.",
                "Lass deine Schultern locker, soweit es sich angenehm anfühlt.",
                "Lege deine Hände bequem ab.",
                "Atme so, wie es gerade von selbst kommt. Du musst deinen Atem nicht vertiefen oder kontrollieren.",
                "Prüfe, ob deine Position angenehm genug ist, um einen Moment darin zu bleiben."
            ),
            "Fertig, sobald du eine möglichst bequeme, sichere Position gefunden hast.",
            "Du musst dich nicht entspannen oder körperliche Symptome zum Verschwinden bringen."
        ),
        ex(
            "panic_thoughts", ExerciseCategory.Thoughts, "Panikgedanken einordnen",
            "Beängstigende Gedanken wahrnehmen, ohne sie automatisch als Tatsachen zu behandeln.",
            listOf(
                "Bemerke, welcher Gedanke dir gerade besonders viel Angst macht.",
                "Benenne ihn innerlich: „Ich habe gerade den Gedanken, dass …“",
                "Erinnere dich daran, dass ein Gedanke zunächst eine Vorstellung oder Befürchtung ist.",
                "Versuche nicht, alle Zweifel sofort aufzulösen.",
                "Richte deine Aufmerksamkeit auf das, was du im Moment tatsächlich weißt."
            ),
            "Fertig, sobald du einen beängstigenden Gedanken als Gedanken erkannt hast.",
            "Du musst dich nicht davon überzeugen, dass alles in Ordnung ist."
        ),
        ex(
            "panic_feelings", ExerciseCategory.Feelings, "Den Moment anerkennen",
            "Die eigene Reaktion wahrnehmen, ohne sich zusätzlich dafür zu verurteilen.",
            listOf(
                "Sage dir innerlich: „Ich erlebe gerade starke Angst.“",
                "Nimm wahr, was gerade in dir vorgeht, ohne jedes Gefühl genau analysieren zu müssen.",
                "Erlaube dir, dass sich dieser Moment schwierig anfühlt.",
                "Sage dir, wenn es für dich passend ist: „Ich muss dieses Gefühl nicht sofort verändern.“",
                "Lass offen, wie lange das Gefühl noch anhält."
            ),
            "Fertig, sobald du anerkannt hast, dass du gerade starke Angst erlebst.",
            "Die Panik muss dafür nicht nachlassen."
        ),
        ex(
            "panic_attention", ExerciseCategory.Attention, "Einen festen Orientierungspunkt finden",
            "Einen überschaubaren Bezugspunkt in der gegenwärtigen Umgebung finden.",
            listOf(
                "Suche dir einen Gegenstand in deiner Umgebung aus.",
                "Schau dir seine Form und Farbe an.",
                "Bemerke, wo sich der Gegenstand befindet.",
                "Wenn es angenehm ist, nimm zusätzlich ein Geräusch aus deiner Umgebung wahr.",
                "Kehre mit deiner Aufmerksamkeit zum Gegenstand zurück, wenn deine Gedanken abschweifen."
            ),
            "Fertig, sobald du einen Gegenstand betrachtet und ein Merkmal daran bemerkt hast.",
            "Du musst dich nicht vollständig auf deine Umgebung konzentrieren können."
        ),
        ex(
            "panic_action", ExerciseCategory.Action, "Den nächsten sicheren Schritt wählen",
            "In einer überwältigenden Situation eine einfache und sichere Handlung finden.",
            listOf(
                "Frage dich: „Was brauche ich gerade unmittelbar?“",
                "Überlege, ob du dich hinsetzen, an einen ruhigeren Ort gehen oder eine vertraute Person kontaktieren möchtest.",
                "Wähle nur einen nächsten Schritt.",
                "Setze ihn um, wenn er sicher und machbar ist.",
                "Wenn du gerade nichts tun kannst, darfst du zunächst an einem sicheren Ort bleiben."
            ),
            "Fertig, sobald du einen passenden nächsten Schritt gewählt hast.",
            "Du musst nicht die gesamte Situation bewältigen."
        ),
        ex(
            "panic_support", ExerciseCategory.SelfSupport, "Ich muss das nicht allein bewältigen",
            "Sich selbst Halt geben und bei Bedarf Unterstützung einbeziehen.",
            listOf(
                "Erinnere dich daran, dass du in diesem Moment Unterstützung verdienen darfst.",
                "Wähle einen Satz, der sich für dich hilfreich anfühlt, beispielsweise: „Ich darf mir jetzt Hilfe holen.“",
                "Wenn du möchtest, kontaktiere eine vertraute Person.",
                "Du kannst ausdrücklich sagen, was du brauchst, etwa: „Kannst du kurz bei mir bleiben?“",
                "Falls du lieber allein bleiben möchtest, überlege, was dir hilft, dich an einem sicheren Ort zu orientieren."
            ),
            "Fertig, sobald du einen unterstützenden Satz gefunden oder entschieden hast, welche Unterstützung du möchtest.",
            "Du musst niemanden kontaktieren, wenn du das gerade nicht möchtest und keine unmittelbare Hilfe benötigst."
        )
    )

    // ---------------------------------------------------------------- Wut
    private val anger = listOf(
        ex(
            "anger_body", ExerciseCategory.Body, "Körperliche Anspannung wahrnehmen",
            "Anspannung bemerken und einen sicheren Umgang damit finden, ohne impulsiv handeln zu müssen.",
            listOf(
                "Nimm wahr, wie sich dein Körper gerade anfühlt.",
                "Achte besonders auf Kiefer, Hände, Schultern und Bauch.",
                "Lockere deine Hände und lass deine Schultern sinken, wenn es angenehm ist.",
                "Wenn du Bewegungsdrang verspürst, gehe ein paar Schritte oder bewege dich auf eine Weise, die dir und anderen nicht schadet.",
                "Du musst die Wut nicht wegdrücken."
            ),
            "Fertig, sobald du eine körperliche Empfindung wahrgenommen und einen Umgang mit der Anspannung gefunden hast."
        ),
        ex(
            "anger_thoughts", ExerciseCategory.Thoughts, "Was hat mich wütend gemacht?",
            "Den Auslöser und die eigene Interpretation unterscheiden, ohne die eigenen Gefühle abzuwerten.",
            listOf(
                "Frage dich: „Welche Situation hat meine Wut ausgelöst?“",
                "Unterscheide zwischen dem, was tatsächlich passiert ist, und dem, was du darin vermutest.",
                "Überlege, welche Erwartung, Grenze oder welches Bedürfnis möglicherweise verletzt wurde.",
                "Erinnere dich daran, dass du nicht sofort entscheiden musst, wer recht hat oder was du tun wirst."
            ),
            "Fertig, sobald du den Auslöser benannt und einen möglichen Gedanken oder eine verletzte Grenze erkannt hast.",
            "Du musst die Situation nicht abschließend bewerten."
        ),
        ex(
            "anger_feelings", ExerciseCategory.Feelings, "Wut anerkennen",
            "Wut zulassen, ohne sie zu verurteilen oder automatisch nach ihr zu handeln.",
            listOf(
                "Sage dir innerlich: „Ich bin gerade wütend.“",
                "Nimm wahr, wie stark sich die Wut anfühlt, ohne sie genau messen zu müssen.",
                "Erinnere dich daran, dass ein Gefühl nicht dasselbe ist wie eine Handlung.",
                "Erlaube dir, wütend zu sein, ohne sofort reagieren zu müssen.",
                "Falls du gerade Abstand brauchst, darfst du ihn dir nehmen."
            ),
            "Fertig, sobald du deine Wut als Gefühl anerkannt hast.",
            "Die Wut muss dafür nicht verschwunden sein."
        ),
        ex(
            "anger_attention", ExerciseCategory.Attention, "Den Fokus bewusst wechseln",
            "Für einen Moment Abstand von der Situation gewinnen, die die Wut verstärkt.",
            listOf(
                "Richte deine Aufmerksamkeit auf einen Gegenstand in deiner Umgebung.",
                "Nimm seine Form, Farbe und Oberfläche wahr.",
                "Höre auf ein Geräusch in deiner Umgebung.",
                "Spüre den Kontakt deiner Füße mit dem Boden.",
                "Wenn deine Gedanken zur Situation zurückkehren, lenke deine Aufmerksamkeit erneut auf deine Umgebung."
            ),
            "Fertig, sobald du einen Gegenstand und ein weiteres Sinnesdetail wahrgenommen hast.",
            "Du musst die Situation nicht vergessen oder deine Wut verlieren."
        ),
        ex(
            "anger_action", ExerciseCategory.Action, "Eine hilfreiche Reaktion wählen",
            "Einen sicheren und bewussten Umgang mit der Situation finden, statt impulsiv zu reagieren.",
            listOf(
                "Frage dich: „Muss ich gerade etwas tun, oder kann ich mir zunächst Abstand nehmen?“",
                "Wenn du sehr aufgebracht bist, verschiebe ein schwieriges Gespräch, sofern das möglich ist.",
                "Überlege, was du eigentlich erreichen möchtest: eine Grenze setzen, etwas klären oder eine Veränderung ansprechen.",
                "Wähle einen nächsten Schritt, der dich diesem Ziel näherbringt, ohne dir oder anderen zu schaden.",
                "Wenn noch keine passende Reaktion möglich ist, darfst du die Entscheidung vertagen."
            ),
            "Fertig, sobald du einen sicheren nächsten Schritt gewählt oder entschieden hast, vorerst nicht zu reagieren.",
            "Du musst den Konflikt nicht sofort lösen."
        ),
        ex(
            "anger_support", ExerciseCategory.SelfSupport, "Meine Wut ernst nehmen",
            "Die eigenen Bedürfnisse und Grenzen anerkennen, ohne sich selbst oder andere abzuwerten.",
            listOf(
                "Frage dich: „Was brauche ich gerade?“",
                "Überlege, ob du Abstand, Ruhe, Verständnis, Fairness oder eine klare Grenze brauchst.",
                "Formuliere einen Satz, der dich unterstützt, beispielsweise: „Meine Gefühle sind wichtig, und ich darf entscheiden, wie ich damit umgehe.“",
                "Wenn eine Grenze verletzt wurde, überlege, wie du sie später ruhig und klar ausdrücken kannst.",
                "Erinnere dich daran, dass du nicht sofort eine perfekte Lösung finden musst."
            ),
            "Fertig, sobald du ein Bedürfnis oder eine Grenze erkannt und einen unterstützenden Satz gefunden hast.",
            "Du musst noch nichts aussprechen oder mit jemandem klären."
        )
    )

    // ---------------------------------------------------------------- Grübeln
    private val rumination = listOf(
        ex(
            "rumination_body", ExerciseCategory.Body, "Aus dem Kopf in den Körper",
            "Die Aufmerksamkeit wieder auf körperliche Empfindungen richten, wenn sich Gedanken ständig wiederholen.",
            listOf(
                "Nimm eine bequeme Position ein.",
                "Spüre, wie deine Füße den Boden berühren.",
                "Bemerke, welche Körperbereiche du gerade wahrnehmen kannst.",
                "Wenn du möchtest, bewege langsam deine Hände oder Schultern.",
                "Richte deine Aufmerksamkeit immer wieder auf die körperliche Wahrnehmung, wenn du bemerkst, dass du erneut ins Grübeln gerätst."
            ),
            "Fertig, sobald du eine körperliche Empfindung wahrgenommen hast.",
            "Du musst deine Gedanken nicht vollständig stoppen."
        ),
        ex(
            "rumination_thoughts", ExerciseCategory.Thoughts, "Kann ich das gerade lösen?",
            "Zwischen hilfreichem Nachdenken und wiederkehrenden Gedankenschleifen unterscheiden.",
            listOf(
                "Bemerke, welcher Gedanke immer wieder auftaucht.",
                "Frage dich: „Gibt es gerade etwas Konkretes, das ich an dieser Situation verändern kann?“",
                "Wenn ja, überlege, welcher kleine nächste Schritt möglich wäre.",
                "Wenn nein, erkenne an, dass weiteres Nachdenken möglicherweise gerade keine neue Antwort liefert.",
                "Erinnere dich daran, dass du eine Frage auch offenlassen darfst."
            ),
            "Fertig, sobald du eingeordnet hast, ob du etwas lösen kannst oder die Frage offenlässt.",
            "Du musst keine endgültige Antwort finden."
        ),
        ex(
            "rumination_feelings", ExerciseCategory.Feelings, "Was steckt hinter dem Grübeln?",
            "Wahrnehmen, welche Gefühle mit den wiederkehrenden Gedanken verbunden sein könnten.",
            listOf(
                "Bemerke, worüber du immer wieder nachdenkst.",
                "Frage dich: „Was fühle ich, wenn ich an dieses Thema denke?“",
                "Vielleicht bemerkst du Angst, Schuld, Traurigkeit, Wut oder Unsicherheit.",
                "Versuche, das Gefühl zu benennen, ohne es sofort erklären oder beseitigen zu müssen.",
                "Erlaube dir, das Gefühl wahrzunehmen, auch wenn die zugrunde liegende Frage ungeklärt bleibt."
            ),
            "Fertig, sobald du ein mögliches Gefühl hinter deinen Gedanken erkannt oder gemerkt hast, dass du es nicht benennen kannst."
        ),
        ex(
            "rumination_attention", ExerciseCategory.Attention, "Einen anderen Fokus finden",
            "Die Aufmerksamkeit bewusst aus der Gedankenschleife auf etwas Gegenwärtiges lenken.",
            listOf(
                "Suche dir etwas in deiner Umgebung aus, auf das du deine Aufmerksamkeit richten möchtest.",
                "Beobachte beispielsweise die Bewegungen eines Baumes, ein Muster oder das Licht im Raum.",
                "Nimm möglichst konkret wahr, was du siehst oder hörst.",
                "Wenn du wieder an dein Grübelthema denkst, bemerke das, ohne dich dafür zu kritisieren.",
                "Kehre zu deiner gewählten Wahrnehmung zurück oder entscheide dich bewusst für eine andere Tätigkeit."
            ),
            "Fertig, sobald du deine Aufmerksamkeit auf etwas anderes gerichtet hast.",
            "Die Gedanken dürfen trotzdem wiederkommen."
        ),
        ex(
            "rumination_action", ExerciseCategory.Action, "Den Gedanken in einen nächsten Schritt verwandeln",
            "Aus wiederholtem Nachdenken eine konkrete, machbare Handlung entwickeln, wenn das Problem beeinflussbar ist.",
            listOf(
                "Frage dich: „Gibt es etwas, das ich wegen dieses Gedankens tatsächlich tun möchte?“",
                "Wenn ja, überlege, was der kleinste sinnvolle Schritt wäre.",
                "Wähle eine konkrete Handlung, beispielsweise eine Information nachzuschauen, eine Frage aufzuschreiben oder ein Gespräch vorzubereiten.",
                "Wenn du gerade nichts unternehmen kannst, entscheide bewusst, das Thema später erneut aufzugreifen, sofern das hilfreich ist.",
                "Wenn keine Handlung erforderlich ist, darfst du dich einer anderen Tätigkeit zuwenden."
            ),
            "Fertig, sobald du einen nächsten Schritt gewählt oder erkannt hast, dass du gerade nichts tun musst.",
            "Du musst das gesamte Problem nicht lösen."
        ),
        ex(
            "rumination_support", ExerciseCategory.SelfSupport, "Ich darf eine Frage offenlassen",
            "Den Druck verringern, jede Unsicherheit sofort auflösen zu müssen.",
            listOf(
                "Bemerke, ob du gerade versuchst, unbedingt eine sichere Antwort zu finden.",
                "Sage dir innerlich: „Ich merke, dass ich darüber immer wieder nachdenke.“",
                "Wähle einen Satz, der dir erlaubt, die Unsicherheit vorerst bestehen zu lassen, beispielsweise: „Ich muss das nicht in diesem Moment klären.“",
                "Erinnere dich daran, dass eine offene Frage nicht automatisch bedeutet, dass du etwas falsch machst.",
                "Entscheide, ob du dich jetzt einer anderen Tätigkeit zuwenden möchtest."
            ),
            "Fertig, sobald du einen Satz gefunden hast, der die Frage offen sein lässt.",
            "Du musst dich dadurch nicht sofort erleichtert fühlen."
        )
    )

    // ---------------------------------------------------------------- Traurigkeit
    private val sadness = listOf(
        ex(
            "sadness_body", ExerciseCategory.Body, "Fürsorglich mit dem Körper umgehen",
            "Eigene körperliche Bedürfnisse bei Traurigkeit wahrnehmen und sich etwas Unterstützung ermöglichen.",
            listOf(
                "Nimm wahr, wie sich dein Körper gerade anfühlt.",
                "Bemerke, ob du beispielsweise erschöpft bist, dich schwer fühlst oder Anspannung spürst.",
                "Frage dich: „Was wäre gerade angenehm oder entlastend für meinen Körper?“",
                "Wähle etwas Passendes, zum Beispiel eine bequemere Position, eine Decke, ein Glas Wasser oder eine sanfte Bewegung.",
                "Du darfst auch einfach in deiner jetzigen Position bleiben, wenn sich das am besten anfühlt."
            ),
            "Fertig, sobald du eine Empfindung wahrgenommen und entschieden hast, was du gerade brauchst.",
            "Du musst dich nicht besser fühlen."
        ),
        ex(
            "sadness_thoughts", ExerciseCategory.Thoughts, "Freundlicher auf dich selbst schauen",
            "Belastende Gedanken über dich selbst erkennen und von Tatsachen unterscheiden.",
            listOf(
                "Bemerke, welche Gedanken deine Traurigkeit begleiten.",
                "Achte besonders auf Gedanken wie „Ich schaffe nichts“, „Alles ist sinnlos“ oder „Ich bin eine Belastung“.",
                "Frage dich: „Ist das eine Tatsache oder eine Bewertung, die ich gerade über mich habe?“",
                "Überlege, ob es eine andere, ebenso mögliche Sichtweise gibt.",
                "Wenn dir keine alternative Sichtweise einfällt, erkenne zunächst nur, dass du diesen Gedanken gerade hast."
            ),
            "Fertig, sobald du einen belastenden Gedanken als Gedanken eingeordnet hast.",
            "Du musst nicht davon überzeugt sein, dass alles gut wird."
        ),
        ex(
            "sadness_feelings", ExerciseCategory.Feelings, "Traurigkeit Raum geben",
            "Traurigkeit wahrnehmen, ohne sie sofort verdrängen oder verändern zu müssen.",
            listOf(
                "Sage dir innerlich: „Ich bin gerade traurig.“",
                "Nimm wahr, wie sich die Traurigkeit für dich zeigt.",
                "Vielleicht spürst du Schwere, Leere, Tränen oder etwas anderes. Du musst nichts Bestimmtes fühlen.",
                "Erlaube dir, traurig zu sein, ohne dich dafür zu verurteilen.",
                "Erinnere dich daran: „Ich muss dieses Gefühl nicht sofort verändern.“"
            ),
            "Fertig, sobald du deine Traurigkeit wahrgenommen und zugelassen hast.",
            "Die Traurigkeit muss weder verschwinden noch leichter werden."
        ),
        ex(
            "sadness_attention", ExerciseCategory.Attention, "Einen kleinen Lichtblick wahrnehmen",
            "Neben der Traurigkeit auch andere Eindrücke wahrnehmen, ohne das Gefühl kleinzureden.",
            listOf(
                "Schau dich in deiner Umgebung um.",
                "Suche nach einer Kleinigkeit, die deine Aufmerksamkeit auf sich zieht.",
                "Das kann eine Farbe, ein Gegenstand, ein Geräusch oder etwas Angenehmes sein.",
                "Nimm diesen Eindruck bewusst wahr.",
                "Wenn du möchtest, richte deine Aufmerksamkeit anschließend auf etwas, das dir normalerweise guttut."
            ),
            "Fertig, sobald du einen Eindruck aus deiner Umgebung wahrgenommen hast.",
            "Du musst nichts Schönes finden. Auch ein neutraler Eindruck genügt."
        ),
        ex(
            "sadness_action", ExerciseCategory.Action, "Eine kleine Sache für dich tun",
            "Eine machbare Handlung finden, die zu deinen aktuellen Möglichkeiten passt.",
            listOf(
                "Frage dich: „Was würde mir gerade guttun oder meinen Alltag ein kleines Stück erleichtern?“",
                "Denke an einfache Möglichkeiten, zum Beispiel etwas trinken, dich hinlegen, etwas essen, ein Fenster öffnen oder eine vertraute Person kontaktieren.",
                "Wähle etwas, das sich für dich machbar anfühlt.",
                "Entscheide, ob du es jetzt tun möchtest oder ob Ruhe gerade wichtiger ist.",
                "Wenn selbst ein kleiner Schritt zu viel erscheint, darfst du die Handlung weglassen."
            ),
            "Fertig, sobald du eine passende Handlung gewählt oder entschieden hast, dass du Ruhe brauchst.",
            "Du musst nichts leisten, um Fürsorge zu verdienen."
        ),
        ex(
            "sadness_support", ExerciseCategory.SelfSupport, "So mit mir sprechen, wie ich es brauche",
            "Dir selbst mit Verständnis begegnen, statt dich für deine Traurigkeit zusätzlich unter Druck zu setzen.",
            listOf(
                "Stelle dir vor, eine Person, die dir wichtig ist, würde sich gerade so traurig fühlen wie du.",
                "Überlege, was du dieser Person sagen würdest.",
                "Wähle daraus einen Satz, den du auch dir selbst zugestehen möchtest.",
                "Beispiele: „Es ist in Ordnung, dass es mir gerade nicht gut geht.“ oder „Ich muss heute nicht alles schaffen.“",
                "Sage dir den Satz innerlich oder lies ihn dir bewusst durch."
            ),
            "Fertig, sobald du einen unterstützenden Satz gefunden oder ausgewählt hast.",
            "Du musst ihn nicht sofort glauben oder dabei etwas Bestimmtes empfinden."
        )
    )

    // ---------------------------------------------------------------- Überforderung
    private val overwhelm = listOf(
        ex(
            "overwhelm_body", ExerciseCategory.Body, "Den Druck im Körper wahrnehmen",
            "Körperliche Anzeichen von Überforderung erkennen und eine angenehmere Haltung finden.",
            listOf(
                "Nimm wahr, wie sich dein Körper gerade anfühlt.",
                "Achte darauf, ob du beispielsweise flach atmest, die Schultern hochziehst, die Zähne zusammenbeißt oder dich unruhig fühlst.",
                "Entscheide, ob du etwas verändern möchtest.",
                "Lass deine Schultern sinken, lockere deine Hände oder verändere deine Sitzposition, wenn es angenehm ist.",
                "Du kannst auch einfach wahrnehmen, was gerade da ist, ohne etwas verändern zu müssen."
            ),
            "Fertig, sobald du eine körperliche Empfindung bemerkt und entschieden hast, ob du darauf reagieren möchtest.",
            "Du musst dich nicht entspannter fühlen."
        ),
        ex(
            "overwhelm_thoughts", ExerciseCategory.Thoughts, "Alles aus dem Kopf heraus sortieren",
            "Gedanken ordnen und zwischen dem unterscheiden, was gerade wichtig ist und was warten kann.",
            listOf(
                "Bemerke, welche Aufgaben, Sorgen oder Erwartungen gleichzeitig in deinem Kopf auftauchen.",
                "Wähle einen Gedanken aus, der dich gerade besonders beschäftigt.",
                "Frage dich: „Muss ich mich wirklich jetzt darum kümmern?“",
                "Ordne den Gedanken einer dieser Möglichkeiten zu: jetzt wichtig, später möglich oder momentan nicht beeinflussbar.",
                "Erinnere dich daran, dass nicht alles gleichzeitig gelöst werden muss."
            ),
            "Fertig, sobald du einen Gedanken eingeordnet hast.",
            "Du musst nicht alle offenen Dinge sortieren."
        ),
        ex(
            "overwhelm_feelings", ExerciseCategory.Feelings, "Überforderung anerkennen",
            "Wahrnehmen, wie belastend die aktuelle Situation ist, ohne dich dafür zu verurteilen.",
            listOf(
                "Sage dir innerlich: „Gerade ist mir alles zu viel.“",
                "Bemerke, welche Gefühle damit verbunden sind. Vielleicht fühlst du dich gestresst, ängstlich, gereizt, traurig oder hilflos.",
                "Versuche nicht, alle Gefühle gleichzeitig zu erklären.",
                "Erkenne an, dass deine aktuelle Belastung real ist.",
                "Erinnere dich daran: „Ich darf einen Schritt zurücktreten, auch wenn noch Dinge offen sind.“"
            ),
            "Fertig, sobald du deine Überforderung oder ein Gefühl dazu wahrgenommen hast.",
            "Die Situation muss sich dadurch nicht weniger belastend anfühlen."
        ),
        ex(
            "overwhelm_attention", ExerciseCategory.Attention, "Nur eine Sache gleichzeitig",
            "Die Aufmerksamkeit von der Gesamtheit aller Anforderungen auf einen einzelnen gegenwärtigen Eindruck lenken.",
            listOf(
                "Schau dich um und wähle einen Gegenstand in deiner Nähe.",
                "Nimm seine Form, Farbe oder Oberfläche bewusst wahr.",
                "Bemerke, wenn deine Gedanken wieder zu anderen Aufgaben springen.",
                "Kehre freundlich zu dem Gegenstand zurück.",
                "Entscheide anschließend, ob du bei dieser Wahrnehmung bleiben oder dich einer einzelnen Tätigkeit zuwenden möchtest."
            ),
            "Fertig, sobald du einen Gegenstand bewusst wahrgenommen hast.",
            "Du musst deine Gedanken nicht vollständig beruhigen."
        ),
        ex(
            "overwhelm_action", ExerciseCategory.Action, "Den nächsten kleinen Schritt wählen",
            "Aus einer überwältigenden Gesamtsituation eine überschaubare nächste Handlung machen.",
            listOf(
                "Frage dich: „Was ist gerade wirklich notwendig?“",
                "Wähle eine einzelne Aufgabe aus, die wichtig ist oder dir Erleichterung verschaffen könnte.",
                "Überlege, was der kleinstmögliche Anfang wäre.",
                "Wenn die Aufgabe zu groß erscheint, teile sie weiter auf. Statt „Wohnung aufräumen“ könnte der erste Schritt beispielsweise sein, nur eine Tasse wegzustellen.",
                "Entscheide, ob du diesen Schritt jetzt angehen möchtest oder zunächst eine Pause brauchst."
            ),
            "Fertig, sobald du einen kleinen Schritt gewählt oder dich für eine Pause entschieden hast.",
            "Du musst nicht die gesamte Aufgabe erledigen."
        ),
        ex(
            "overwhelm_support", ExerciseCategory.SelfSupport, "Ich darf meine Grenzen beachten",
            "Den eigenen Ansprüchen mit Verständnis begegnen und sich erlauben, Belastung zu begrenzen.",
            listOf(
                "Frage dich: „Was erwarte ich gerade alles von mir?“",
                "Überlege, ob diese Erwartungen angesichts deiner aktuellen Kraft realistisch sind.",
                "Stelle dir vor, eine vertraute Person wäre in derselben Situation. Was würdest du ihr zugestehen?",
                "Wähle einen Satz, der dir ebenfalls entgegenkommen darf, zum Beispiel: „Ich muss nicht alles gleichzeitig schaffen.“",
                "Überlege, ob du etwas verschieben, vereinfachen oder um Unterstützung bitten möchtest."
            ),
            "Fertig, sobald du einen unterstützenden Satz gewählt oder eine Erwartung erkannt hast, die du verringern möchtest.",
            "Du musst nicht sofort alle Anforderungen verändern."
        )
    )

    // ---------------------------------------------------------------- Schlaflosigkeit
    private val insomnia = listOf(
        ex(
            "insomnia_body", ExerciseCategory.Body, "Den Körper nicht zum Schlafen zwingen",
            "Körperliche Anspannung wahrnehmen und dem Körper erlauben, sich auszuruhen, ohne Schlaf erzwingen zu müssen.",
            listOf(
                "Nimm wahr, wie du gerade liegst oder sitzt.",
                "Bemerke, ob du beispielsweise deine Schultern anspannst, die Zähne zusammenbeißt oder unruhig liegst.",
                "Verändere deine Position, wenn sich das angenehmer anfühlt.",
                "Lass deine Muskeln dort los, wo es dir leichtfällt. Du musst nicht den gesamten Körper entspannen.",
                "Lass deinen Atem so fließen, wie er von selbst kommt. Du musst ihn weder vertiefen noch kontrollieren."
            ),
            "Fertig, sobald du eine Empfindung wahrgenommen und deine Position bei Bedarf angepasst hast.",
            "Du musst dich nicht vollständig entspannen und nicht einschlafen."
        ),
        ex(
            "insomnia_thoughts", ExerciseCategory.Thoughts, "Den Schlafdruck loslassen",
            "Belastende Gedanken über das Wachliegen erkennen und den Druck reduzieren, jetzt unbedingt schlafen zu müssen.",
            listOf(
                "Bemerke, welche Gedanken dir durch den Kopf gehen.",
                "Vielleicht denkst du: „Morgen bin ich völlig fertig“, „Ich muss jetzt endlich schlafen“ oder „Warum funktioniert das bei mir nicht?“",
                "Erkenne, dass solche Gedanken zusätzlichen Druck erzeugen können.",
                "Sage dir innerlich: „Ich bin gerade wach. Ich muss das nicht in diesem Moment lösen.“",
                "Wenn du an den nächsten Tag denkst, erinnere dich daran, dass du dich morgen mit den dann tatsächlichen Anforderungen beschäftigen kannst."
            ),
            "Fertig, sobald du einen belastenden Gedanken erkannt und einen sanfteren Umgang damit gewählt hast.",
            "Du musst deine Gedanken nicht vollständig beruhigen."
        ),
        ex(
            "insomnia_feelings", ExerciseCategory.Feelings, "Die Unruhe der Nacht anerkennen",
            "Gefühle wahrnehmen, die beim Wachliegen auftreten, ohne gegen sie ankämpfen zu müssen.",
            listOf(
                "Frage dich vorsichtig: „Wie fühlt sich das Wachliegen gerade für mich an?“",
                "Vielleicht bemerkst du Frustration, Angst, Einsamkeit, Traurigkeit oder Ungeduld.",
                "Wenn du ein Gefühl erkennst, benenne es innerlich.",
                "Wenn du nichts Eindeutiges wahrnimmst, musst du nichts herausfinden.",
                "Erinnere dich daran: „Es ist verständlich, dass mich das Wachliegen belastet. Ich muss dieses Gefühl nicht sofort verändern.“"
            ),
            "Fertig, sobald du ein Gefühl wahrgenommen oder anerkannt hast, dass es unklar ist.",
            "Das Gefühl muss nicht verschwinden."
        ),
        ex(
            "insomnia_attention", ExerciseCategory.Attention, "Bei einer ruhigen Wahrnehmung bleiben",
            "Die Aufmerksamkeit von kreisenden Gedanken auf eine neutrale oder angenehme Wahrnehmung lenken.",
            listOf(
                "Wähle etwas Ruhiges, worauf du deine Aufmerksamkeit richten möchtest.",
                "Du kannst beispielsweise das Gewicht deiner Decke, ein leises Umgebungsgeräusch oder das natürliche Kommen und Gehen deines Atems wahrnehmen.",
                "Konzentriere dich auf eine einzelne Eigenschaft dieser Wahrnehmung.",
                "Wenn deine Gedanken abschweifen, bemerke das, ohne dich zu kritisieren.",
                "Kehre zur gewählten Wahrnehmung zurück, wenn du möchtest. Wenn sie unangenehm wird, wähle etwas anderes."
            ),
            "Fertig, sobald du eine ruhige Wahrnehmung bemerkt und deine Aufmerksamkeit darauf gerichtet hast.",
            "Du musst nicht schläfrig werden."
        ),
        ex(
            "insomnia_action", ExerciseCategory.Action, "Entscheiden, was jetzt hilfreich ist",
            "Eine passende Entscheidung für die aktuelle Schlafsituation treffen, statt sich hilflos im Wachliegen festzufahren.",
            listOf(
                "Frage dich: „Was brauche ich gerade – Ruhe, eine Veränderung oder weniger Druck?“",
                "Überlege, ob es sich angenehm anfühlt, noch etwas liegen zu bleiben.",
                "Wenn du zunehmend frustriert oder unruhig wirst, kannst du aufstehen und bei gedämpftem Licht etwas Ruhiges tun.",
                "Wähle eine wenig anregende Tätigkeit, beispielsweise etwas Ruhiges lesen oder leise Musik hören.",
                "Kehre ins Bett zurück, wenn du wieder schläfriger wirst. Du musst dabei nicht auf die Uhr schauen."
            ),
            "Fertig, sobald du entschieden hast, ob du liegen bleibst oder kurz aufstehst.",
            "Du musst nicht sofort einschlafen. Wenn Schlaflosigkeit häufig auftritt oder dich stark belastet, kann es sinnvoll sein, sie ärztlich abklären zu lassen."
        ),
        ex(
            "insomnia_support", ExerciseCategory.SelfSupport, "Auch eine schwierige Nacht bewältigen",
            "Mit sich selbst verständnisvoll umgehen und die Angst vor den Folgen einer schlechten Nacht etwas entlasten.",
            listOf(
                "Bemerke, ob du dich wegen des Wachliegens unter Druck setzt oder dir Vorwürfe machst.",
                "Stelle dir vor, eine Person, die dir wichtig ist, würde gerade dieselbe Nacht erleben.",
                "Überlege, was du ihr sagen würdest, ohne ihre Schwierigkeiten kleinzureden.",
                "Wähle einen Satz, den du dir selbst zugestehen möchtest, zum Beispiel: „Ich darf mich ausruhen, auch wenn ich gerade nicht schlafe.“",
                "Erinnere dich daran, dass eine schwierige Nacht nicht bedeutet, dass du versagt hast."
            ),
            "Fertig, sobald du einen unterstützenden Satz gewählt oder dir erlaubt hast, ohne Vorwürfe wach zu sein.",
            "Du musst dich dadurch nicht sofort sicherer oder müder fühlen."
        )
    )

    // ---------------------------------------------------------------- Einsamkeit
    private val loneliness = listOf(
        ex(
            "loneliness_body", ExerciseCategory.Body, "Sich selbst Geborgenheit geben",
            "Körperliche Bedürfnisse wahrnehmen und ein Gefühl von Komfort oder Sicherheit unterstützen.",
            listOf(
                "Nimm wahr, wie sich dein Körper gerade anfühlt.",
                "Bemerke, ob du dich angespannt, unruhig, erschöpft oder vielleicht leer fühlst.",
                "Überlege, was sich gerade angenehm anfühlen könnte.",
                "Wähle etwas Passendes: Wickle dich in eine Decke, halte eine warme Tasse, setze dich bequem hin oder lege eine Hand auf deinen Arm, wenn sich das gut anfühlt.",
                "Lass dir die Freiheit, eine andere Möglichkeit zu wählen oder nichts zu verändern."
            ),
            "Fertig, sobald du eine Empfindung wahrgenommen und eine Form von Komfort gewählt hast – oder nichts davon möchtest.",
            "Du musst dich nicht geborgen oder weniger einsam fühlen."
        ),
        ex(
            "loneliness_thoughts", ExerciseCategory.Thoughts, "Einsamkeit ist nicht mein Wert",
            "Gedanken über den eigenen Wert und die Zugehörigkeit hinterfragen, ohne die Einsamkeit kleinzureden.",
            listOf(
                "Bemerke, welche Gedanken auftauchen, wenn du dich einsam fühlst.",
                "Vielleicht denkst du: „Niemand interessiert sich für mich“ oder „Ich gehöre nirgendwo dazu“.",
                "Frage dich: „Beschreibt dieser Gedanke meine aktuelle Situation oder bewertet er mich als Person?“",
                "Überlege, ob du zwischen dem Gefühl, gerade allein zu sein, und deinem Wert als Mensch unterscheiden kannst.",
                "Wenn dir keine andere Sichtweise einfällt, erkenne zunächst nur, dass Einsamkeit solche Gedanken hervorrufen kann."
            ),
            "Fertig, sobald du einen Gedanken über dich geprüft hast: Tatsache oder Bewertung?",
            "Du musst dich nicht davon überzeugen, dass du nicht einsam bist."
        ),
        ex(
            "loneliness_feelings", ExerciseCategory.Feelings, "Das Bedürfnis hinter der Einsamkeit erkennen",
            "Wahrnehmen, welche Gefühle und Bedürfnisse mit der Einsamkeit verbunden sind.",
            listOf(
                "Sage dir innerlich: „Ich fühle mich gerade einsam.“",
                "Bemerke, welche Gefühle damit verbunden sind. Vielleicht sind es Traurigkeit, Sehnsucht, Leere, Enttäuschung oder der Wunsch, verstanden zu werden.",
                "Frage dich: „Was fehlt mir gerade am meisten?“",
                "Vielleicht wünschst du dir Nähe, ein Gespräch, Verständnis oder einfach die Anwesenheit eines anderen Menschen.",
                "Erkenne dieses Bedürfnis an, ohne dich dafür zu verurteilen."
            ),
            "Fertig, sobald du ein Gefühl oder Bedürfnis hinter der Einsamkeit benannt hast.",
            "Du musst dieses Bedürfnis nicht sofort erfüllen können."
        ),
        ex(
            "loneliness_attention", ExerciseCategory.Attention, "Verbindung zur Umgebung wahrnehmen",
            "Die eigene Aufmerksamkeit bewusst auf die gegenwärtige Umgebung richten, ohne die Einsamkeit verdrängen zu müssen.",
            listOf(
                "Schau dich an dem Ort um, an dem du gerade bist.",
                "Suche einen Gegenstand, eine Pflanze, ein Fenster oder etwas anderes, das deine Aufmerksamkeit auf sich zieht.",
                "Nimm eine konkrete Eigenschaft wahr, zum Beispiel eine Farbe, eine Form oder ein Geräusch.",
                "Bemerke auch, ob du etwas wahrnimmst, das dir vertraut oder angenehm ist.",
                "Wenn deine Gedanken wieder zur Einsamkeit zurückkehren, ist das in Ordnung. Du kannst deine Aufmerksamkeit erneut auf deine Umgebung richten."
            ),
            "Fertig, sobald du einen Eindruck aus deiner Umgebung wahrgenommen hast.",
            "Du musst dich dadurch nicht verbundener fühlen."
        ),
        ex(
            "loneliness_action", ExerciseCategory.Action, "Eine kleine Verbindung ermöglichen",
            "Eine Form von Kontakt oder Nähe finden, die den eigenen Bedürfnissen und Möglichkeiten entspricht.",
            listOf(
                "Frage dich: „Würde mir gerade Kontakt zu einem anderen Menschen guttun?“",
                "Überlege, welche Form von Kontakt passend wäre. Vielleicht möchtest du jemandem schreiben, telefonieren, eine vertraute Person treffen oder dich unter Menschen aufhalten.",
                "Wähle eine Möglichkeit, die sich machbar anfühlt.",
                "Falls du Kontakt aufnehmen möchtest, kannst du eine einfache Nachricht schreiben, beispielsweise: „Hey, hast du gerade etwas Zeit zum Reden?“",
                "Wenn du gerade keinen Kontakt möchtest oder niemand erreichbar ist, überlege, was dir stattdessen guttun könnte."
            ),
            "Fertig, sobald du eine Form von Kontakt gewählt oder entschieden hast, was du stattdessen brauchst.",
            "Du musst keine Antwort erhalten und keinen Kontakt erzwingen."
        ),
        ex(
            "loneliness_support", ExerciseCategory.SelfSupport, "Ich darf mir Nähe wünschen",
            "Das eigene Bedürfnis nach Nähe ernst nehmen und sich nicht für Einsamkeit oder Sehnsucht verurteilen.",
            listOf(
                "Überlege, was du dir gerade von anderen Menschen wünschst.",
                "Erinnere dich daran, dass der Wunsch nach Nähe, Verständnis und Zugehörigkeit menschlich ist.",
                "Bemerke, ob du dir selbst Vorwürfe machst, weil du einsam bist.",
                "Wähle einen Satz, der dir mit Verständnis begegnet, zum Beispiel: „Es ist verständlich, dass ich mir Nähe wünsche.“",
                "Wenn es dir hilft, überlege, welche Person oder welcher Ort dir grundsätzlich ein Gefühl von Verbundenheit geben kann."
            ),
            "Fertig, sobald du einen unterstützenden Satz gewählt oder eine Person oder einen Ort der Verbundenheit benannt hast.",
            "Du musst dich nicht sofort weniger einsam fühlen."
        )
    )

    // ---------------------------------------------------------------- Ich weiß nicht genau
    private val unknown = listOf(
        ex(
            "unknown_body", ExerciseCategory.Body, "Wahrnehmen, was gerade da ist",
            "Einen ersten Zugang zum eigenen Zustand finden, ohne das Gefühl bereits benennen zu müssen.",
            listOf(
                "Nimm wahr, wie dein Körper sich gerade anfühlt.",
                "Bemerke, ob du beispielsweise Anspannung, Müdigkeit, Unruhe, Schwere oder gar nichts Bestimmtes wahrnimmst.",
                "Richte deine Aufmerksamkeit auf eine Körperstelle, die du gerade gut spüren kannst.",
                "Beschreibe die Empfindung innerlich möglichst einfach, etwa als warm, kalt, schwer, ruhig oder angespannt.",
                "Wenn du keine eindeutige Empfindung findest, kannst du auch das einfach feststellen."
            ),
            "Fertig, sobald du eine Empfindung wahrgenommen oder gemerkt hast, dass du gerade nichts Eindeutiges spürst.",
            "Du musst nicht herausfinden, was diese Empfindung bedeutet."
        ),
        ex(
            "unknown_thoughts", ExerciseCategory.Thoughts, "Den inneren Zustand beschreiben",
            "Erste Hinweise auf den eigenen Zustand erkennen, ohne sich zu einer eindeutigen Erklärung zu zwingen.",
            listOf(
                "Frage dich: „Was geht mir gerade durch den Kopf?“",
                "Bemerke, ob deine Gedanken eher ruhig, schnell, unübersichtlich, leer oder kreisend sind.",
                "Versuche, einen Gedanken zu benennen, der gerade besonders präsent ist.",
                "Frage dich, ob dieser Gedanke möglicherweise mit deinem aktuellen Zustand zusammenhängt.",
                "Wenn du keinen Zusammenhang erkennst, lasse die Frage offen."
            ),
            "Fertig, sobald du einen Gedanken oder die Art deiner Gedanken wahrgenommen hast.",
            "Du musst keine Erklärung für deinen Zustand finden."
        ),
        ex(
            "unknown_feelings", ExerciseCategory.Feelings, "Ich muss es nicht sofort wissen",
            "Unsicherheit über die eigenen Gefühle zulassen, ohne zusätzlichen Druck aufzubauen.",
            listOf(
                "Sage dir innerlich: „Ich weiß gerade nicht genau, was ich fühle.“",
                "Bemerke, ob dieser Umstand selbst etwas in dir auslöst, beispielsweise Unsicherheit, Frustration oder Gleichgültigkeit.",
                "Versuche nicht, sofort ein eindeutiges Gefühl auszuwählen.",
                "Erlaube dir, mehrere Empfindungen gleichzeitig zu haben oder gerade keinen klaren Zugang zu deinen Gefühlen zu finden.",
                "Erinnere dich daran, dass du deinen Zustand nicht vollständig verstehen musst, um für dich sorgen zu dürfen."
            ),
            "Fertig, sobald du anerkannt hast, dass dein Gefühl gerade unklar ist.",
            "Du musst kein bestimmtes Gefühl benennen."
        ),
        ex(
            "unknown_attention", ExerciseCategory.Attention, "Die Umgebung als Orientierung nutzen",
            "Einen einfachen Bezug zum gegenwärtigen Moment finden, wenn der eigene innere Zustand unklar ist.",
            listOf(
                "Schau dich in deiner Umgebung um.",
                "Wähle einen Gegenstand, den du gut sehen kannst.",
                "Nimm seine Farbe, Form oder Position bewusst wahr.",
                "Höre anschließend auf ein Geräusch oder spüre, wie deine Füße den Boden berühren.",
                "Bemerke, dass du deine Aufmerksamkeit zwischen deiner Umgebung und deinem inneren Erleben bewegen kannst."
            ),
            "Fertig, sobald du einen Gegenstand und eine weitere Sinneswahrnehmung bemerkt hast.",
            "Du musst dich danach weder ruhiger noch klarer fühlen."
        ),
        ex(
            "unknown_action", ExerciseCategory.Action, "Herausfinden, was ich gerade brauche",
            "Eine passende nächste Handlung wählen, auch wenn das eigene Gefühl noch unklar ist.",
            listOf(
                "Frage dich: „Was könnte mir gerade helfen, unabhängig davon, was ich genau fühle?“",
                "Überlege, ob du ein grundlegendes Bedürfnis wahrnimmst: Ruhe, Bewegung, etwas zu trinken, Nahrung, Kontakt oder Abstand.",
                "Wenn du unsicher bist, wähle eine einfache Möglichkeit, die sich angenehm oder sinnvoll anfühlt.",
                "Du könntest beispielsweise ein Glas Wasser trinken, dich bequemer hinsetzen, das Fenster öffnen oder eine vertraute Person kontaktieren.",
                "Wenn du gerade nichts tun möchtest, darfst du auch zunächst bleiben, wo du bist."
            ),
            "Fertig, sobald du eine hilfreiche Handlung gewählt oder entschieden hast, vorerst nichts zu verändern.",
            "Du musst nicht wissen, warum du etwas brauchst."
        ),
        ex(
            "unknown_support", ExerciseCategory.SelfSupport, "Mir selbst Orientierung geben",
            "Freundlich mit sich selbst umgehen, wenn die eigenen Gefühle schwer einzuordnen sind.",
            listOf(
                "Bemerke, ob du von dir erwartest, genau erklären zu können, wie es dir geht.",
                "Erinnere dich daran, dass Gefühle und innere Zustände nicht immer eindeutig sind.",
                "Überlege, welcher Satz dir in dieser Situation Halt geben könnte.",
                "Wähle beispielsweise: „Ich darf mir Zeit lassen, um herauszufinden, wie es mir geht.“",
                "Wenn es dir hilft, überlege, was du einer vertrauten Person sagen würdest, die ihren eigenen Zustand gerade nicht versteht."
            ),
            "Fertig, sobald du einen unterstützenden Satz gewählt oder dir erlaubt hast, deinen Zustand nicht verstehen zu müssen.",
            "Du musst keine Klarheit erreichen."
        )
    )

    val all: List<Situation> = listOf(
        Situation("anxiety", "Angst", Icons.Outlined.Shield, shown(anxiety)),
        Situation("panic", "Panik", Icons.Outlined.Waves, shown(panic)),
        Situation("anger", "Wut", Icons.Outlined.LocalFireDepartment, shown(anger)),
        Situation("rumination", "Grübeln", Icons.Outlined.Psychology, shown(rumination)),
        Situation("sadness", "Traurigkeit", Icons.Outlined.SentimentDissatisfied, shown(sadness)),
        Situation("overwhelm", "Überforderung", Icons.Outlined.Layers, shown(overwhelm)),
        Situation("insomnia", "Schlaflosigkeit", Icons.Outlined.NightsStay, shown(insomnia)),
        Situation("loneliness", "Einsamkeit", Icons.Outlined.PersonOutline, shown(loneliness)),
        Situation(
            "unknown", "Allgemein", Icons.Outlined.Explore, shown(unknown),
            intro = "Das ist okay. Du musst gerade nicht wissen, was los ist. " +
                "Wir können einfach mit dem Moment anfangen."
        )
    )

    fun fromId(id: String?): Situation? = all.firstOrNull { it.id == id }

    fun findExercise(id: String?): Exercise? =
        all.flatMap { it.exercises }.firstOrNull { it.id == id }
}
