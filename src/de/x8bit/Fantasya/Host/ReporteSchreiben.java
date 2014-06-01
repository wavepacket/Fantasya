package de.x8bit.Fantasya.Host;


import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Atlantis.Messages.SysErr;
import de.x8bit.Fantasya.Atlantis.Messages.SysMsg;
import de.x8bit.Fantasya.Atlantis.Messages.ZATMsg;
import de.x8bit.Fantasya.Host.Reports.ReportCR;
import de.x8bit.Fantasya.Host.Reports.ReportNR;
import de.x8bit.Fantasya.Host.Reports.ReportXML;
import de.x8bit.Fantasya.Host.Reports.ReportZR;
import de.x8bit.Fantasya.Host.Reports.SyntaxHighlightingNR;
import de.x8bit.Fantasya.Host.Reports.Zipping;
import de.x8bit.Fantasya.util.Codierung;

public class ReporteSchreiben
{
	public ReporteSchreiben() {
		new ZATMsg("erstelle Reporte");
        
        new de.x8bit.Fantasya.Host.EVA.Reporte();
        // PreAction() und PostAction() wurden implizit über den EVABase-Konstruktor aufgerufen
        
		new ReportXML(new Partei());	// "world.xml" erzeugen
	}
	
	/**
     * @deprecated wird derzeit wohl mit fehlenden Berechnungen kollidieren, die ansonsten in EVA.ReporteSchreiben.PreAction gemacht werden - nicht getestet.
     * @param partei 
     */
    public ReporteSchreiben(int partei) {
		Partei p = Partei.getPartei(partei);
		if (p == null) { new SysErr("Partei '" + Codierung.toBase36(partei) + "' ist nicht vorhanden"); return; }
		new SysMsg("erstelle Report für " + p);
		
		new ReportCR(p);
		new ReportNR(p);
		new ReportZR(p);
        new SyntaxHighlightingNR(p);
		new ReportXML(p);
		new Zipping(p);
	}
	
}
