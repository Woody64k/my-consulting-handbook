/**
 * Klasse für einen Studenten.
 */
export class Student {
	private static readonly TEXT = "Keine Matrikelnummer.";

	/**
	 * Konstruktor.
	 * @param name Name des Studenten
	 * @param matrikelNr Metrikelnummer der Strudenten
	 */
	constructor(private readonly name: string, private readonly matrikelNr: number) { }
	/**
 	 * Liefert eine Beschriftung für den Studenten.
	 * @return Beschriftung für den Studenten.
	 */
	public getLabel(): string {
		return `Student ${this.name} mit Matrikelnummer: ${this.matrikelNr}`
	}
}