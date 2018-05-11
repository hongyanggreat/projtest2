/*
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.gk.htc.ahp.brand.jsmpp.session.connection.socket;

import java.io.IOException;
import java.net.Socket;

import com.gk.htc.ahp.brand.jsmpp.session.connection.Connection;
import com.gk.htc.ahp.brand.jsmpp.session.connection.ConnectionFactory;
import java.net.SocketAddress;

/**
 * @author uudashr
 *
 */
public class SocketConnectionFactory implements ConnectionFactory {

    private static final SocketConnectionFactory connFactory = new SocketConnectionFactory();

    public static SocketConnectionFactory getInstance() {
        return connFactory;
    }

    private SocketConnectionFactory() {
    }

    @Override
    public Connection createConnection(String host, int port) throws IOException {
        Socket sk = new Socket(host, port);
        SocketAddress skAdd = sk.getRemoteSocketAddress();
        return new SocketConnection(sk, skAdd);
    }
}
