import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

public class Event {
	protected final WatchEvent.Kind<?> kind;
	protected final Path folder;
	protected final Path filename;
	
	Event(WatchEvent.Kind<?> kind, Path folder, Path filename) {
		this.kind = kind;
		this.folder = folder;
		this.filename = filename;
	}
	
	File getFile () {
		return new File(folder.resolve(filename).toString());
	}
}