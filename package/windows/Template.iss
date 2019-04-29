;This file will be executed next to the application bundle image
;I.e. current directory will contain folder Template with application files
[Setup]
AppId={{com.template}}
AppName=Template
AppVersion=0.0.1
AppVerName=Template 0.0.1
AppPublisher=Goldovi
AppComments=Template Platform
AppCopyright=(c) 2019 Goldovi
AppPublisherURL=http://www.goldovi.com/
AppSupportURL=http://www.goldovi.com/
AppUpdatesURL=http://www.goldovi.com/
DefaultDirName={localappdata}\Template
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
OutputBaseFilename=Template-0.0.1
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
SetupIconFile=Template\Template.ico
UninstallDisplayIcon={app}\Template.ico
UninstallDisplayName=Template
WizardImageStretch=No
WizardSmallImageFile=Template-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=x64


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "Template\Template.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "Template\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\Template"; Filename: "{app}\Template.exe"; IconFilename: "{app}\Template.ico"; Check: returnTrue()
;Name: "{commondesktop}\Template"; Filename: "{app}\Template.exe";  IconFilename: "{app}\Template.ico"; Check: returnFalse()
Name: "{userdesktop}\Template"; Filename: "{app}\Template.exe"; IconFilename: "{app}\Template.ico"; Tasks: desktopicon
Name: "{group}\Uninstall Template"; Filename: "{uninstallexe}";IconFilename: "{app}\Template.ico";


[Run]
Filename: "{app}\Template.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\Template.exe"; Description: "{cm:LaunchProgram,Template}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\Template.exe"; Parameters: "-install -svcName ""Template"" -svcDesc ""Template from Goldovi"" -mainExe ""Template.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\Template.exe"; Parameters: "-uninstall -svcName Template -stopOnUninstall"; Check: returnFalse()

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
