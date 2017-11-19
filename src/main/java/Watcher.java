import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.function.Consumer;

public class Watcher implements Runnable {
	final protected Path folder;
	final protected WatchService watcher;
	final protected Consumer<Event> handler;
	
	Watcher (String path, Consumer<Event> handler) throws IOException {
		folder = FileSystems.getDefault().getPath(path);
		watcher = FileSystems.getDefault().newWatchService();
		
		folder.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
		
		this.handler = handler;
	}

	public void run() {
		for (;;) {
			WatchKey key;
			
			try {
				key = watcher.take();
			} catch (InterruptedException e) {
				return;
			}
			
			for (WatchEvent<?> event: key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();
				
				if (kind == OVERFLOW) continue;
				
				WatchEvent<Path> ev = (WatchEvent<Path>)event;
				
				handler.accept(new Event(kind, folder, ev.context()));
			}
			
			if (!key.reset()) break;
		}
	}
}
