import os
import csv
from datetime import datetime
import shutil
import tkinter as tk
from tkinter import filedialog, messagebox, ttk
import sys

def get_image_path(image_name):
    if getattr(sys, 'frozen', False):  
        base_path = os.path.dirname(sys.executable)
    else:
        base_path = os.path.dirname(os.path.abspath(__file__))
        image_folder = os.path.join(base_path, "bilder")
    return os.path.join(image_folder, image_name)

# Update image assignments
GRUENES_BILD = get_image_path("erlaubt.png")
GELBES_BILD = get_image_path("intern.png")
ROTES_BILD = get_image_path("nicht_erlaubt.png")
GRAUES_BILD = get_image_path("unknown.png")

def check_images_exist():
    missing_files = []
    for image in [GRUENES_BILD, GELBES_BILD, ROTES_BILD, GRAUES_BILD]:
        if not os.path.exists(image):
            missing_files.append(image)

    if missing_files:
        message = f"Fehler: Die folgenden Bilddateien fehlen:\n" + "\n".join(missing_files)
        print(message)
        if 'tkinter' in sys.modules:  # Falls GUI verwendet wird, zeige Fehlermeldung in Messagebox
            messagebox.showerror("Fehlende Bilder", message)
        sys.exit(1)

def process_csv(csv_file, output_dir):
    # Zeitstempel für die Fehlerdatei
    zeitstempel = datetime.now().strftime("%Y-%m-%d%H%M%S")

    # Ausgabeordner erstellen
    os.makedirs(output_dir, exist_ok=True)

    # Fehlerprotokoll erstellen
    fehler_file = f"000_Fehler_{zeitstempel}.csv"
    fehler_path = os.path.join(output_dir, fehler_file)

    # CSV-Datei einlesen und verarbeiten
    with open(csv_file, newline='', encoding='utf-8') as csvfile, open(fehler_path, 'w', newline='', encoding='utf-8') as fehlercsv:
        reader = csv.DictReader(csvfile, delimiter=';')
        fehler_writer = csv.writer(fehlercsv, delimiter=';')  # Setze Semikolon als Trennzeichen
        
        # Header für die Fehlerdatei schreiben
        fehler_writer.writerow(['Benutzername', 'DSGVO_extern', 'DSGVO_intern', 'ID'])

        for row in reader:
            extern = row['DSGVO_extern'].strip().lower()
            intern = row['DSGVO_intern'].strip().lower()
            id_nutzer = row['ID']
            benutzername = row['Benutzername']
            
            # Überprüfen auf zulässige Kombinationen
            if extern == "ja" and intern == "ja":
                bild = GRUENES_BILD
            elif extern == "nein" and intern == "ja":
                bild = GELBES_BILD
            elif extern == "nein" and intern == "nein":
                bild = ROTES_BILD
            else:
                # Unzulässige Kombination, Fehler protokollieren
                bild = GRAUES_BILD
                fehler_writer.writerow([benutzername, row['DSGVO_extern'], row['DSGVO_intern'], id_nutzer])
            
            # Bild in den Ausgabeordner kopieren und umbenennen
            ziel_pfad = os.path.join(output_dir, f"{id_nutzer}.png")
            shutil.copyfile(bild, ziel_pfad)

    print(f"Verarbeitung abgeschlossen. Ergebnisse im Ordner: {output_dir}")

def run_cli():
    # Benutzereingaben über die Kommandozeile
    csv_file = input("Geben Sie den Pfad zur CSV-Datei ein: ")
    csv_dir = os.path.dirname(csv_file)
    output_dir = os.path.join(csv_dir, f"DSGVO4WebUntis_{datetime.now().strftime('%Y-%m-%d%H%M%S')}")

    # Prüfe, ob die Bilder vorhanden sind
    check_images_exist()

    process_csv(csv_file, output_dir)

class DSGVOApp:
    def __init__(self, root):
        self.root = root
        self.root.title("DSGVO Profilbilder Generator")
        self.root.geometry("600x400")  # Größere Fensterabmessungen

        # Eingabefelder
        self.csv_file_path = tk.StringVar()
        self.output_folder_var = tk.StringVar(value="DSGVO4WebUntis_$[timestamp]")

        # Frame für besseres Layout
        main_frame = ttk.Frame(root, padding="10")
        main_frame.grid(row=0, column=0, sticky=(tk.W, tk.E, tk.N, tk.S))
        root.columnconfigure(0, weight=1)
        root.rowconfigure(0, weight=1)

        # Detaillierte Beschreibung für die CSV-Eingabe
        tk.Label(main_frame, text="Eingabe durch csv-Datei mit Semikolon getrennten Spalten:", 
                  wraplength=550, justify="left").grid(row=0, column=0, columnspan=3, sticky="w", pady=(0, 5))
        ttk.Label(main_frame, text="Benutzername;DSGVOextern;DSGVOintern;ID", 
                  font=("Courier", 10)).grid(row=1, column=0, columnspan=3, sticky="w", pady=(0, 10))

        # CSV-Datei Auswahl
        ttk.Label(main_frame, text="Pfad zur CSV-Datei:").grid(row=2, column=0, sticky="w")
        ttk.Entry(main_frame, textvariable=self.csv_file_path, width=50).grid(row=2, column=1, sticky="we")
        ttk.Button(main_frame, text="Durchsuchen", command=self.browse_csv).grid(row=2, column=2, padx=(5, 0))

        # Ausgabeordner Information
        ttk.Label(main_frame, text="Ausgabe geschieht im Verzeichnis der CSV-Datei als Ordner", 
                  wraplength=550, justify="left").grid(row=3, column=0, columnspan=3, sticky="w", pady=(10, 5))

        # Ordnername Eingabe
        ttk.Label(main_frame, text="Ordnername:").grid(row=4, column=0, sticky="w")
        ttk.Entry(main_frame, textvariable=self.output_folder_var, width=50).grid(row=4, column=1, columnspan=2, sticky="we")

        # Erklärung für den Platzhalter
        ttk.Label(main_frame, text="Verwenden Sie $[timestamp] als Platzhalter für das aktuelle Datum und die Uhrzeit.", 
                  wraplength=550, justify="left").grid(row=5, column=0, columnspan=3, sticky="w", pady=(5, 10))

        # Start-Button
        ttk.Button(main_frame, text="Starten", command=self.process_csv).grid(row=6, column=1, pady=10)

        # Konfiguriere Spaltengewichte
        main_frame.columnconfigure(1, weight=1)

        # Prüfe die Bilder direkt beim Start der GUI
        check_images_exist()

    def browse_csv(self):
        file_path = filedialog.askopenfilename(filetypes=[("CSV-Dateien", "*.csv")])
        if file_path:
            self.csv_file_path.set(file_path)

    def process_csv(self):
        csv_file = self.csv_file_path.get()
        output_dir_template = self.output_folder_var.get()

        if not csv_file:
            messagebox.showerror("Fehler", "Bitte wählen Sie eine CSV-Datei aus.")
            return

        timestamp = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
        output_dir_name = output_dir_template.replace("$[timestamp]", timestamp)

        csv_dir = os.path.dirname(csv_file)
        output_dir = os.path.join(csv_dir, output_dir_name)

        process_csv(csv_file, output_dir)
        messagebox.showinfo("Erfolg", f"Verarbeitung abgeschlossen. Ergebnisse im Ordner: {output_dir}")

if __name__ == "__main__":
    if len(sys.argv) > 1 and sys.argv[1] == "--cli":
        run_cli()
    else:
        root = tk.Tk()
        app = DSGVOApp(root)
        root.mainloop()