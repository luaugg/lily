defmodule Lily.Commands.List do
  import Lily.Commands.Server, only: [add_command: 2]
  import Nostrum.Api, only: [create_message: 2]

  def start_link(init \\ :ok) do
    add_command("pong", &ping/2)
  end

  defp ping(message, args), do:
    create_message(message.channel_id, "Pong!")
end
