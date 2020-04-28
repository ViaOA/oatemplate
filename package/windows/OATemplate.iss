;This file will be executed next to the application bundle image
;I.e. current directory will contain folder OATemplate with application files
[Setup]
AppId={{com.template}}
AppName=OATemplate
AppVersion=0.0.1
AppVerName=OATemplate 0.0.1
AppPublisher=Goldovi
AppComments=OATemplate Platform
AppCopyright=(c) 2019 Goldovi
AppPublisherURL=http://www.goldovi.com/
AppSupportURL=http://www.goldovi.com/
AppUpdatesURL=http://www.goldovi.com/
DefaultDirName={localappdata}\OATemplate
DisableStartupPrompt=No
DisableDirPage=No
DisableProgramGroupPage=No
DisableReadyPage=No
DisableFinishedPage=No
DisableWelcomePage=No
DefaultGroupName=Goldovi AppStore
;Optional License
LicenseFile=license.txt
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=OATemplate-0.0.1
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
SetupIconFile=OATemplate\OATemplate.ico
UninstallDisplayIcon={app}\OATemplate.ico
UninstallDisplayName=OATemplate
WizardImageStretch=No
WizardSmallImageFile=OATemplate-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=x64


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "OATemplate\OATemplate.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "OATemplate\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\OATemplate"; Filename: "{app}\OATemplate.exe"; IconFilename: "{app}\OATemplate.ico"; Check: returnTrue()
;Name: "{commondesktop}\OATemplate"; Filename: "{app}\OATemplate.exe";  IconFilename: "{app}\OATemplate.ico"; Check: returnFalse()
Name: "{userdesktop}\OATemplate"; Filename: "{app}\OATemplate.exe"; IconFilename: "{app}\OATemplate.ico"; Tasks: desktopicon
Name: "{group}\Uninstall OATemplate"; Filename: "{uninstallexe}";IconFilename: "{app}\OATemplate.ico";


[Run]
Filename: "{app}\OATemplate.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\OATemplate.exe"; Description: "{cm:LaunchProgram,OATemplate}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\OATemplate.exe"; Parameters: "-install -svcName ""OATemplate"" -svcDesc ""OATemplate from Goldovi"" -mainExe ""OATemplate.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\OATemplate.exe"; Parameters: "-uninstall -svcName OATemplate -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
