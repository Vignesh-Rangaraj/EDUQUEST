type NetworkListener = (isOnline: boolean) => void;

class NetworkService {
  private listeners: Set<NetworkListener> = new Set();
  private onlineState: boolean = typeof navigator !== 'undefined' ? navigator.onLine : true;

  constructor() {
    if (typeof window !== 'undefined') {
      window.addEventListener('online', this.handleOnline);
      window.addEventListener('offline', this.handleOffline);
    }
  }

  private handleOnline = () => {
    this.onlineState = true;
    this.notifyListeners();
  };

  private handleOffline = () => {
    this.onlineState = false;
    this.notifyListeners();
  };

  private notifyListeners() {
    this.listeners.forEach((listener) => listener(this.onlineState));
  }

  public isOnline(): boolean {
    return this.onlineState;
  }

  public isOffline(): boolean {
    return !this.onlineState;
  }

  public subscribe(listener: NetworkListener): () => void {
    this.listeners.add(listener);
    // Send immediate initial status
    listener(this.onlineState);

    return () => {
      this.listeners.delete(listener);
    };
  }
}

export const networkService = new NetworkService();
